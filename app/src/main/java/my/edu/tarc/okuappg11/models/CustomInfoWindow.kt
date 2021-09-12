package my.edu.tarc.okuappg11.models

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.HomeActivity

class CustomInfoWindow(context: Context) : GoogleMap.InfoWindowAdapter  {

    var mContext = context
    var mWindow: View = (context as Activity).layoutInflater.inflate(R.layout.marker_info_window, null)

    private fun rendowWindowText(marker: Marker, view: View){

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvSnippet = view.findViewById<TextView>(R.id.tvSnippet)

        tvTitle.text = marker.title
        tvSnippet.text = marker.snippet

    }



    override fun getInfoWindow(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoContents(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }
}