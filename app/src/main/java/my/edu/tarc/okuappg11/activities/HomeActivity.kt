package my.edu.tarc.okuappg11.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    val TAG = "MainTag"

    private lateinit var locationRequest: LocationRequest
    private val REQUEST_CHECK_SETTING = 1001
    private val permissionCode = 101

    fun destroyActivity(){
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        @Suppress("DEPRECIATON")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            )
//        }

        val bottomNavigationView = binding.bottomNavigationView
        val navController = findNavController(R.id.mainContainerView)

        //Need this when there is ActionBar
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.HomeFragment,
//                R.id.SearchFragment,
//                R.id.NearMeFragment,
//                R.id.ProfileFragment
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)
        checkLocationSetting()
        askPermission()
    }

    private fun askPermission(){
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                permissionCode
            )
            return
        }
    }

    private fun checkLocationSetting() {
        locationRequest = LocationRequest.create()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 2000
            maxWaitTime = 10 * 1000
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
                Toast.makeText(this@HomeActivity, "GPS is On", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "checkSetting: GPS On")
            } catch (e: ApiException) {

                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            this@HomeActivity,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTING -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Toast.makeText(this@HomeActivity, "GPS is Turned on", Toast.LENGTH_SHORT)
                            .show()
                    }
                    Activity.RESULT_CANCELED -> {
                        Toast.makeText(
                            this@HomeActivity,
                            "GPS is Required to use this app",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Press Logout Instead", Toast.LENGTH_SHORT).show()
    }
}