package com.example.grozydriver

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grozydriver.AdapterLocationRecycle
import com.example.drivergrozy.model.Orders
import com.example.grozydriver.databinding.ActivityNavigationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat

data class BestRouteLengthandNeighbour(val bestneighbour: IntArray, val bestroutelength: Double)
data class HillClimbingOutput(val finalsolution: IntArray, val finalroutelength: Double)
class NavigationActivity: AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var lastlocation: Location
    private lateinit var destlocation: Array<Double>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityNavigationBinding
    private var addressGeo: ArrayList<Address>? = ArrayList()
    private lateinit var adapterLocationRecycle: AdapterLocationRecycle
    private lateinit var layout: SlidingUpPanelLayout
    private lateinit var routelength: DoubleArray
    private var routes: Array<DoubleArray> = arrayOf()


    companion object{
        var datasetchged: Boolean = false
        private const val LOCATION_REQUEST_CODE = 1
        val hashMap: HashMap<String, Marker> = HashMap()
        var addressList: ArrayList<Address>? = ArrayList()
        var addressFirebase: ArrayList<String>? = ArrayList()
        var recipeArrayList: ArrayList<Orders> = ArrayList()
        var addressID: ArrayList<String>? = ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onResume(){
        super.onResume()
        if(datasetchged == true){
            adapterLocationRecycle.notifyDataSetChanged()
            datasetchged = false
            if(addressFirebase!!.size == 0 && addressList!!.size == 0 && recipeArrayList.size == 0){
                binding.noMoreOrder.visibility = View.VISIBLE
                binding.slidingup.panelHeight = 120
            }
            else{
                binding.noMoreOrder.visibility = View.GONE
                binding.slidingup.panelHeight = 360

            }

        }
    }

    override fun onPause() {
        super.onPause()
    }

    private fun loadOrdersFromFirebase() {

        val ref = FirebaseDatabase.getInstance("https://testing-16c76-default-rtdb.firebaseio.com").getReference("Orders")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(addressFirebase!!.isNotEmpty()){
                    addressFirebase!!.clear()
                }
                if(addressID!!.isNotEmpty()){
                    addressID!!.clear()
                }
                recipeArrayList.clear()
                for(ds in snapshot.children){
                    val modelImage = ds.getValue(Orders::class.java)
                    if (modelImage != null) {
                        if(modelImage.orderStatus == "In Progress") {
                            recipeArrayList.add(modelImage!!)
                            addressFirebase!!.add(modelImage.address!!)
                            addressID!!.add(modelImage.orderId!!)
                        }
                    }
                }
                val linearLayoutManager = object: LinearLayoutManager(this@NavigationActivity){
                    override fun canScrollVertically(): Boolean {
                        return false
                    }

                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }
                }
                if(addressList!!.isNotEmpty()) {
                    addressList!!.clear()
                }
                if(routes!!.isNotEmpty()){
                    routes = dropallroutes(routes)
                }
                Log.d("TAG", recipeArrayList.size.toString())
                var checklocation: ArrayList<Int> = ArrayList()
                for(index in 0 until addressFirebase!!.size){
                    var result: Boolean = searchLocation(addressFirebase!![index])
                    if(!result){
                        checklocation.add(index)
                    }
                }
                if(checklocation.size != 0) {
                    var index = checklocation.size
                    while (index >= 0) {
                        Toast.makeText(this@NavigationActivity, addressFirebase!![checklocation[index]]+" not found",Toast.LENGTH_SHORT).show()
                        addressFirebase!!.removeAt(checklocation[index])
                        index -= 1
                    }
                }
                if(recipeArrayList!!.size != 1 && recipeArrayList!!.size != 0) {
                    findShortestDistance()
                    Log.d("TAG","Triggered")
                }

                adapterLocationRecycle = AdapterLocationRecycle(layout.context,addressFirebase,addressList,addressID , recipeArrayList)
                if(addressFirebase!!.size == 0 && addressList!!.size == 0 && recipeArrayList.size == 0){
                    binding.noMoreOrder.visibility = View.VISIBLE
                    binding.slidingup.panelHeight = 120
                }
                else{
                    binding.noMoreOrder.visibility = View.GONE
                    binding.slidingup.panelHeight = 360
                }
                binding.locationRecycle.layoutManager = linearLayoutManager
                binding.locationRecycle.adapter = adapterLocationRecycle

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    private fun setUpMap() {
        layout = findViewById(R.id.slidingup)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)

            return
        }
        mMap.isMyLocationEnabled = true
        lastlocation = Location("")
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastlocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeUserMarkerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }
        loadOrdersFromFirebase()

        binding.trackUserLocation.setOnClickListener() {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastlocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    placeUserMarkerOnMap(currentLatLng)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
        binding.backBtn.setOnClickListener(){
            finish()
        }
        binding.refresh.setOnClickListener(){
            loadOrdersFromFirebase()

        }

    }
    private fun findShortestDistance(){

        var after: Boolean = false
        var j: Int = 0
        var i:Int = 0
        var route: DoubleArray = doubleArrayOf()
        var distance: Double
        while(i <= addressList!!.size){
            route = droproute(route)

            while (j <= addressList!!.size){
                if(i == 0){
                    if(j == i) {
                        distance= getDistance(lastlocation.latitude,
                            lastlocation.longitude,
                            lastlocation.latitude,
                            lastlocation.longitude)
                        route = addDistance(route, distance)
                    }
                    else{
                        var end = addressList!![j-1]
                        distance = getDistance(lastlocation.latitude, lastlocation.longitude, end.latitude, end.longitude)
                        route = addDistance(route, distance)
                    }
                }
                else{
                    if(j == i){
                        var start = addressList!![i-1]
                        distance = getDistance(start.latitude, start.longitude, lastlocation.latitude, lastlocation.longitude)
                        route = addDistance(route, distance)
                        after = true
                    }
                    else{
                        if(after){
                            var start = addressList!![i - 1]
                            var end = addressList!![j-1]
                            distance = getDistance(start.latitude,
                                start.longitude,
                                end.latitude,
                                end.longitude)
                            route = addDistance(route, distance)
                        }
                        else {
                            var start = addressList!![i - 1]
                            var end = addressList!![j]
                            distance = getDistance(start.latitude,
                                start.longitude,
                                end.latitude,
                                end.longitude)
                            route = addDistance(route, distance)
                        }
                    }
                }
                j += 1

            }
            after = false
            j = 0
            i += 1
            routes = addElement(routes, route)

        }
        var bestsolution: IntArray = hillClimbing(routes).finalsolution
        var bestroutedistance: Double = hillClimbing(routes).finalroutelength
        addressList = sortAddress(bestsolution)
        addressFirebase = sortRoute(bestsolution)
        addressID = sortID(bestsolution)
    }

    private fun sortAddress(solution: IntArray): ArrayList<Address>{
        var sortedRoute: ArrayList<Address> = ArrayList()
        for (sol in solution){
            if(sol !=0) {
                sortedRoute.add(addressList!![sol-1])
            }
        }
        addressList!!.clear()
        return sortedRoute
    }
    private fun sortRoute(solution: IntArray): ArrayList<String>{
        var sortedRoute: ArrayList<String> = ArrayList()
        for (sol in solution){
            if(sol !=0) {
                sortedRoute.add(addressFirebase!![sol-1])
            }
        }
        addressFirebase!!.clear()
        return sortedRoute
    }
    private fun sortID(solution: IntArray): ArrayList<String>{
        var sortedRoute: ArrayList<String> = ArrayList()
        for (sol in solution){
            if(sol !=0) {
                sortedRoute.add(addressID!![sol-1])
            }
        }
        addressID!!.clear()
        return sortedRoute
    }

    private fun placeUserMarkerOnMap(currentLatLng: LatLng) {
        if(hashMap.containsKey("User Location")){
            val marker: Marker = hashMap["User Location"]!!
            marker.remove()
        }
        val markerOptions = MarkerOptions().position(currentLatLng)
        markerOptions.title("$currentLatLng")
        val marker: Marker = mMap.addMarker(markerOptions)!!
        hashMap["User Location"] = marker
    }

    private fun placeSearchedMarkerOnMap(currentLatLng: LatLng, location: String) {
        if(hashMap.containsKey("Search Location")){
            val marker: Marker = hashMap["Search Location"]!!
            marker.remove()
        }
        val markerOptions = MarkerOptions().position(currentLatLng)
        markerOptions.title(location)
        val marker: Marker = mMap.addMarker(markerOptions)!!
        hashMap["Search Location"] = marker
    }

    override fun onMarkerClick(p0: Marker) = false

    private fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double):Double{
        var Radius: Int = 6371
        var dlat: Double = Math.toRadians(lat2-lat1)
        var dlon: Double = Math.toRadians(lon2-lon1)
        var a: Double = (Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dlon / 2)
                * Math.sin(dlon / 2)))
        var c: Double = 2 * Math.asin(Math.sqrt(a))
        var valueResult: Double = Radius * c
        var Distance: Double = valueResult / 1
        var newformat: DecimalFormat = DecimalFormat("###.####")
        newformat.roundingMode = RoundingMode.CEILING
        var parsedDistance: Double = newformat.format(Distance).toDouble()
        //var kmInDec: Int = newformat.format(parsedDistance).toInt()

        return parsedDistance
    }

    private fun searchLocation(location: String): Boolean{
        location
        var locationresult = false
        if(location == ""){
            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
        }
        else{
            val geocoder = Geocoder(this)
            try{
                addressGeo = geocoder.getFromLocationName(location, 1) as ArrayList<Address>?

            }catch (e: IOException){
                e.printStackTrace()
            }
            if(addressGeo!!.size != 0){
                addressList!!.add(addressGeo!!.get(0))
                var address = addressGeo!!.get(0)
                val LatLng = LatLng(address.latitude,address.longitude)
                placeSearchedMarkerOnMap(LatLng, location)
                locationresult = true
            }

        }
        return locationresult

    }
    private fun addDistance(arr: DoubleArray, element: Double): DoubleArray {
        val mutableArray = arr.toMutableList()
        mutableArray.add(element)
        return mutableArray.toDoubleArray()
    }

    private fun addElement(arr: Array<DoubleArray>, element: DoubleArray): Array<DoubleArray> {
        val mutableArray = arr.toMutableList()
        mutableArray.add(element)
        return mutableArray.toTypedArray()
    }

    private fun addNeighbour(arr: Array<IntArray>, element: IntArray): Array<IntArray> {
        val mutableArray = arr.toMutableList()
        mutableArray.add(element)
        return mutableArray.toTypedArray()
    }

    private fun addCity(arr: IntArray, element: Int): IntArray {
        val mutableArray = arr.toMutableList()
        mutableArray.add(element)
        return mutableArray.toIntArray()
    }

    private fun dropCity(arr: IntArray, element: Int): IntArray {
        val mutableArray = arr.toMutableList()
        mutableArray.remove(element)
        return mutableArray.toIntArray()
    }
    private fun dropallroutes(route: Array<DoubleArray>): Array<DoubleArray> {
        val mutableArray = route.toMutableList()
        mutableArray.clear()
        return mutableArray.toTypedArray()
    }
    private fun droproute(route: DoubleArray): DoubleArray {
        val mutableArray = route.toMutableList()
        mutableArray.clear()
        return mutableArray.toDoubleArray()
    }
    private fun addroutes(routelength2: DoubleArray){
        routes = addElement(routes, routelength2)
    }
    private fun randomSolution(tsp: Array<DoubleArray>): IntArray{
        var cities: IntArray = (tsp.indices).toList().toIntArray()
        var solution: IntArray = intArrayOf()
        for(i in tsp.indices){
            if(i == 0){
                solution = addCity(solution,0)
                cities = dropCity(cities,0)
            }
            else {
                var randomCity: Int = cities.random()
                solution = addCity(solution, randomCity)
                cities = dropCity(cities, randomCity)
            }

        }
        return solution

    }
    private fun routeLength(tsp: Array<DoubleArray>, solution: IntArray): Double{
        var routeLength: Double = 0.0
        var i: Int = 0
        while(i < solution.size){
            if(i != solution.size-1) {
                routeLength += tsp[solution[i]][solution[i + 1]]
            }
            else routeLength += tsp[solution[i]][solution[0]]
            i += 1
        }
        return routeLength
    }

    private fun getNeighbours(solution: IntArray): Array<IntArray>{
        var neighbours: Array<IntArray> = arrayOf()
        for (i in solution.indices){
            if(i!=0) {
                for (j in i + 1 until solution.size) {
                    val neighbour: IntArray = solution.copyOf()
                    neighbour[i] = solution[j]
                    neighbour[j] = solution[i]
                    neighbours = addNeighbour(neighbours, neighbour)
                }
            }
        }
        return neighbours

    }

    private fun getBestNeighbours(tsp: Array<DoubleArray>, neighbours: Array<IntArray>): BestRouteLengthandNeighbour {
        var bestRouteLength: Double = routeLength(tsp, neighbours[0])
        var bestNeighbour: IntArray = intArrayOf()
        bestNeighbour = neighbours[0]

        for (neighbour in neighbours){
            val currentRouteLength: Double = routeLength(tsp, neighbour)
            if (currentRouteLength < bestRouteLength){
                bestRouteLength = currentRouteLength
                bestNeighbour = neighbour
            }

        }

        return BestRouteLengthandNeighbour(bestNeighbour,bestRouteLength)
    }

    private fun hillClimbing(tsp: Array<DoubleArray>): HillClimbingOutput{
        var currentSolution: IntArray = randomSolution(tsp)
        var currentRouteLength: Double = routeLength(tsp, currentSolution)
        var neighbours: Array<IntArray> = getNeighbours(currentSolution)
        var bestneighbour: IntArray = getBestNeighbours(tsp,neighbours).bestneighbour
        var bestNeighbourRouteLength: Double = getBestNeighbours(tsp,neighbours).bestroutelength

        while (bestNeighbourRouteLength < currentRouteLength){
            currentSolution = bestneighbour
            currentRouteLength = bestNeighbourRouteLength
            neighbours = getNeighbours(currentSolution)
            bestneighbour = getBestNeighbours(tsp,neighbours).bestneighbour
            bestNeighbourRouteLength = getBestNeighbours(tsp,neighbours).bestroutelength
        }

        return HillClimbingOutput(currentSolution, currentRouteLength)

    }
    /* *********
    for (i in routes.indices)
    {
        var j = 0
        while (j < routes[i]?.size)
        {
            print(routes[i][j].toString() + " ")
            j++
        }
        println()
    }

     */

}