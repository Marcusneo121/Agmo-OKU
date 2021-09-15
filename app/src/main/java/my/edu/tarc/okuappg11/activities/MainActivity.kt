package my.edu.tarc.okuappg11.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var userID: String? = null
    private var userRole: String? = null

    val TAG = "MainTag"

    private lateinit var locationRequest: LocationRequest
    private val REQUEST_CHECK_SETTING = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        //checkLocationSetting()

        stayLogin()
    }

    private fun checkLocationSetting() {
        locationRequest = LocationRequest.create()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 3000
            fastestInterval = 1000
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(applicationContext)
                .checkLocationSettings(builder.build())

        result.addOnCompleteListener {
            try {
                val response: LocationSettingsResponse = it.getResult(ApiException::class.java)
                //Toast.makeText(this@MainActivity, "GPS is On", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "checkSetting: GPS On")
            } catch (e: ApiException) {

                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            this@MainActivity,
                            REQUEST_CHECK_SETTING
                        )
                        Log.d(TAG, "checkSetting: RESOLUTION_REQUIRED")
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // USER DEVICE DOES NOT HAVE LOCATION OPTION
                    }
                }
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            REQUEST_CHECK_SETTING -> {
//                when (resultCode) {
//                    Activity.RESULT_OK -> {
//                        Toast.makeText(this@MainActivity, "GPS is Turned on", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                    Activity.RESULT_CANCELED -> {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "GPS is Required to use this app",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun stayLogin() {
        var getSharePref = getSharedPreferences("sharedLogin", Context.MODE_PRIVATE)
        var username = getSharePref.getString("EMAIL", null)
        var pass = getSharePref.getString("PASS", null)

        if (username != null && pass != null) {
            fAuth.signInWithEmailAndPassword(username.toString(), pass.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        userID = fAuth.currentUser?.uid
                        fStore.collection("users").document(userID!!).get()
                            .addOnSuccessListener { it ->
                                userRole = it.get("userType").toString()
                                if (userRole == "OKU" || userRole == "Normal") {
                                    val intent = Intent(this, HomeActivity::class.java)
                                    startActivity(intent)

                                } else if (userRole == "Admin") {
                                    val intent = Intent(this, AdminHomeActivity::class.java)
                                    startActivity(intent)

                                }
                            }
                    }
                }
        } else {
            return
        }
    }
}