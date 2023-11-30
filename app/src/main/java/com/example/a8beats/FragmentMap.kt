package com.example.a8beats

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.search.*
import com.mapbox.search.result.SearchResult
import kotlinx.android.synthetic.main.fragment_map.*
import org.json.JSONObject

private const val MARKER_ICON_BLUE = "marker_icon_blue"
private const val MARKER_ICON_RED = "marker_icon_red"

class FragmentMap : Fragment(R.layout.fragment_map), OnMapReadyCallback, PermissionsListener {

    private lateinit var map: MapboxMap
    private lateinit var context_: Context
    private lateinit var permissionManager: PermissionsManager
    private lateinit var categorySearchEngine: CategorySearchEngine
    private lateinit var queue: RequestQueue
    private lateinit var symbolManager: SymbolManager
    private val sharedData: SharedDataViewModel by activityViewModels()

    private val searchCallback = object : SearchCallback {
        override fun onError(e: Exception) {
            Toast.makeText(
                context_,
                context_.getString(R.string.error_occured) + e.cause,
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
            getClubs(map, results)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context_ = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(context_, getString(R.string.access_token_mapbox))
        queue = Volley.newRequestQueue(context_)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val searchBut = view.findViewById<AppCompatImageButton>(R.id.search_button)
        val category = view.findViewById<Spinner>(R.id.category)
        ArrayAdapter.createFromResource(
            context_,
            R.array.choices,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            category.adapter = adapter
        }

        searchBut.setOnClickListener {
            Toast.makeText(context_, getString(R.string.searching), Toast.LENGTH_SHORT).show()
            categorySearchEngine = MapboxSearchSdk.createCategorySearchEngine()
            search(
                searchCallback,
                category.selectedItem.toString(),
                map.locationComponent.lastKnownLocation!!,
                20
            )
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    @SuppressLint("InflateParams")
    override fun onMapReady(mapboxMap: MapboxMap) {
        mapboxMap.let { map = it }
        mapboxMap.setStyle(Style.DARK) {
            enableLocation()
            symbolManager = SymbolManager(mapView, map, it)
        }
        sharedData.data.observe(viewLifecycleOwner, {
            addMarkers(map, it)
        })
    }

    @SuppressWarnings("MissingPermissions")
    private fun enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {

            // Get an instance of the component
            val locationComponent: LocationComponent = map.locationComponent

            // Activate with a built LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(
                    context_,
                    map.style!!
                ).build()
            )

            // Enable to make component visible
            if (ActivityCompat.checkSelfPermission(
                    context_,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context_,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    activity,
                    getString(R.string.loc_permissions),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                locationComponent.isLocationComponentEnabled = true
            }

            // Set the component's camera mode
            locationComponent.cameraMode = CameraMode.TRACKING_GPS_NORTH

            // Set the component's render mode
            locationComponent.renderMode = RenderMode.NORMAL


            //set the map view on the user location
            if (locationComponent.lastKnownLocation != null) {
                val camera = CameraPosition.Builder()
                    .target(LatLng(locationComponent.lastKnownLocation))
                    .zoom(9.0)
                    .build()
                map.animateCamera(CameraUpdateFactory.newCameraPosition(camera), 7000)
            }

        } else {
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(activity)
        }
    }

    override fun onExplanationNeeded(p0: MutableList<String>?) {
        Toast.makeText(
            context_,
            getString(R.string.loc_work_properly),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPermissionResult(p0: Boolean) {
        if (p0) {
            enableLocation()
        }
    }

    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onStart() {
        mapView.onStart()
        super.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onDestroyView() {
        mapView.onPause()
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun search(scb: SearchCallback, category: String, location: Location?, max: Int) {

        if (location != null) {
            val searchReqTask = categorySearchEngine.search(
                category, CategorySearchOptions(
                    proximity = Point.fromLngLat(location.longitude, location.latitude),
                    limit = max
                ), scb
            )
        } else {
            val searchReqTask = categorySearchEngine.search(
                category, CategorySearchOptions(
                    limit = max
                ), scb
            )
        }
    }

    //function for adding markers in the map
    private fun addMarkers(mapboxMap: MapboxMap, clubList: List<JSONObject>) {

        mapboxMap.setStyle(Style.DARK) {
            it.addImage(
                MARKER_ICON_BLUE,
                BitmapFactory.decodeResource(requireActivity().resources, R.drawable.blue_marker)
            )
            it.addImage(
                MARKER_ICON_RED,
                BitmapFactory.decodeResource(requireActivity().resources, R.drawable.red_marker)
            )

            symbolManager.deleteAll()
            symbolManager = SymbolManager(mapView, map, it)
            symbolManager.iconAllowOverlap = true

            for (elem in clubList) {
                if (elem.getString("owner") != "none") {
                    symbolManager.create(
                        SymbolOptions()
                            .withGeometry(
                                Point.fromLngLat(
                                    elem.getDouble("lon"),
                                    elem.getDouble("lat")
                                )
                            )
                            .withTextField(elem.getString("name"))
                            .withTextOffset(arrayOf(0.0f, 1.0f))
                            .withTextColor("#FF3737")
                            .withIconImage(MARKER_ICON_RED)
                    )
                } else {
                    symbolManager.create(
                        SymbolOptions()
                            .withGeometry(
                                Point.fromLngLat(
                                    elem.getDouble("lon"),
                                    elem.getDouble("lat")
                                )
                            )
                            .withTextField(elem.getString("name"))
                            .withTextOffset(arrayOf(0.0f, 1.0f))
                            .withTextColor("#8AD0F7")
                            .withIconImage(MARKER_ICON_BLUE)
                    )
                }
            }

            symbolManager.addClickListener { symbol ->
                val item = clubList[symbol.id.toInt()]
                val text = item.getString("name").plus("\n").plus(item.getString("address"))
                Toast.makeText(context_, text, Toast.LENGTH_SHORT).show()
                true
            }

            symbolManager.addLongClickListener { symbol ->
                clubInfo(clubList[symbol.id.toInt()])
                true
            }
        }
    }


    //function for get signed clubs and then mark them on the map
    private fun getClubs(mapboxMap: MapboxMap, results: List<SearchResult>) {
        val respList = Response.Listener<JSONObject> {
            val response = it.getJSONArray("clubs")
            val clubList = Utils.createClubListFromResults(results, response)
            sharedData.setData(clubList)
            addMarkers(mapboxMap, clubList)
        }
        queue.add(RequestManager.get(context_, RequestManager.TYPE_CLUBS, "italia", respList))
    }

    private fun clubInfo(club: JSONObject) {
        val dialog = DialogClubInfo(queue, club)
        dialog.show(requireActivity().supportFragmentManager, "Send Message")
    }

}