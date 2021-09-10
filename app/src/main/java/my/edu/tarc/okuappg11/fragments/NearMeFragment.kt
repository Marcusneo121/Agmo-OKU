package my.edu.tarc.okuappg11.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import my.edu.tarc.okuappg11.databinding.FragmentNearMeBinding

class NearMeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentNearMeBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap

    private val Jabil = com.google.android.gms.maps.model.LatLng(5.3267065, 100.2820637)
    //private lateinit var mapArrayList: ArrayList<MapArrayList>
    private var locationArrayList: ArrayList<com.google.android.gms.maps.model.LatLng>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNearMeBinding.inflate(inflater, container, false)
        return binding.root
    }
}