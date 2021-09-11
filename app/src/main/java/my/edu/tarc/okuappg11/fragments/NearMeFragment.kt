package my.edu.tarc.okuappg11.fragments

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.EventDetailsActivity
import my.edu.tarc.okuappg11.activities.HomeActivity
import my.edu.tarc.okuappg11.activities.testActivity
import my.edu.tarc.okuappg11.data.AllEventsArrayList
import my.edu.tarc.okuappg11.databinding.FragmentNearMeBinding
import my.edu.tarc.okuappg11.models.CustomInfoWindow

class NearMeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private var _binding: FragmentNearMeBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private var latLngValue: LatLng? = null

    private lateinit var fStore: FirebaseFirestore
    private var currentLocation: Location? = null
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private val permissionCode = 101

    private lateinit var allEventsArrayList: ArrayList<AllEventsArrayList>
    //private var locationArrayList: ArrayList<com.google.android.gms.maps.model.LatLng>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        fetchLocation()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val map = binding.mapViewNearMe

        map.onCreate(savedInstanceState)
        map.onResume()
        map.getMapAsync(this)

//        locationArrayList = ArrayList()
//        locationArrayList!!.add(Jabil)
    }

    override fun onMapReady(map: GoogleMap) {

        googleMap = map
//        for(i in locationArrayList!!.indices){
//            //add marker
//            googleMap.addMarker(MarkerOptions().position(locationArrayList!![i]).title("Marker $i"))
//            googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f))
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList!![i]))
//        }

//        map.let{
//            googleMap = it
//        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                permissionCode
            )
            return
        }

        val task = fusedLocationProvider.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Toast.makeText(
                    this.context, currentLocation!!.latitude.toString() + " "
                            + currentLocation!!.longitude.toString(), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getCurrentLocation(){
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            //val latLng: com.google.android.gms.maps.model.LatLng
            val latLng = com.google.android.gms.maps.model.LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
//            if(currentLocation != null){
//                latLng = com.google.android.gms.maps.model.LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
//            } else {
//                latLng = com.google.android.gms.maps.model.LatLng(6.1303069, 100.3835099)
//            }

            for(i in allEventsArrayList.indices){
                latLngValue = LatLng(allEventsArrayList[i].latitude, allEventsArrayList[i].longitude)
                googleMap.setOnInfoWindowClickListener(this)
                googleMap.setInfoWindowAdapter(CustomInfoWindow(requireContext()))
                googleMap.addMarker(MarkerOptions().position(latLngValue)
                    .title(allEventsArrayList[i].eventName).snippet(allEventsArrayList[i].eventID))
                    .setIcon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_round_emoji_events_24))
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f))
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngValue))
            }

            val markerOptions = MarkerOptions().position(latLng)
                .title("Current Location").icon(bitmapDescriptorFromVector(requireContext(), R.drawable.human_me))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
            googleMap.addMarker(markerOptions)
        }, 2000)
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            permissionCode -> if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                fetchLocation()
            }
        }
    }

    override fun onInfoWindowClick(marker: Marker) {

            //Toast.makeText(context, "${marker.snippet}", Toast.LENGTH_SHORT).show()

            val intent = Intent(this.context, EventDetailsActivity::class.java)
            val snippet = marker.snippet
            intent.putExtra("EventUID", snippet)

            if (marker.title == "Current Location"){
                Toast.makeText(context, "You are here!", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(intent)
            }
    }

    fun getAllEvents(){
        Toast.makeText(context, "It runs here", Toast.LENGTH_LONG).show()
        fStore = FirebaseFirestore.getInstance()
        val collectionReference = fStore.collection("events")

        collectionReference.addSnapshotListener{ snapshot, e->
            if(e!=null){
                Toast.makeText(context, "$e", Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            if(snapshot != null){
                allEventsArrayList = arrayListOf()
                val document = snapshot.documents
                document.forEach{
                    val mapDetails = it.toObject(AllEventsArrayList::class.java)
                    if(mapDetails != null){
                        mapDetails.eventID = it.id
                        allEventsArrayList.add(mapDetails)
                    }
                }
            }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if(allEventsArrayList.isEmpty()){
                Toast.makeText(context, "Array is empty", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Array is not empty", Toast.LENGTH_LONG).show()
            }

            getCurrentLocation()
        }, 2000)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNearMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAllEvents()
    }
}


