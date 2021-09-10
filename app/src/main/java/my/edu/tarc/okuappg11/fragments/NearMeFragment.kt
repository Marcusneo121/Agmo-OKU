package my.edu.tarc.okuappg11.fragments

import android.content.pm.PackageManager
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.R
import my.edu.tarc.okuappg11.databinding.FragmentNearMeBinding

class NearMeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentNearMeBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap

    private var currentLocation: Location? = null
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private val permissionCode = 101

    private val Jabil = com.google.android.gms.maps.model.LatLng(5.3267065, 100.2820637)
    //private lateinit var mapArrayList: ArrayList<MapArrayList>
    private var locationArrayList: ArrayList<com.google.android.gms.maps.model.LatLng>? = null

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

        locationArrayList = ArrayList()
        locationArrayList!!.add(Jabil)
    }

    override fun onMapReady(map: GoogleMap) {

        googleMap = map
        for(i in locationArrayList!!.indices){
            //add marker
            googleMap.addMarker(MarkerOptions().position(locationArrayList!![i]).title("Marker $i"))
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList!![i]))
        }

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
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
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
            val markerOptions = MarkerOptions().position(latLng).title("Current Location")
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f))
            googleMap.addMarker(markerOptions)

        }, 5000)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNearMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCurrentLocation()
    }
}