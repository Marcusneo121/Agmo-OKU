package my.edu.tarc.okuappg11.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityAddEventBinding
import my.edu.tarc.okuappg11.databinding.ActivityMapAutocompleteBinding
import java.util.*


class MapAutocompleteActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMapAutocompleteBinding
    private var eventLocality:String?= null
    private var latitude:String?= null
    private var longitude:String?= null
    private var filterLatitude:String? = null
    private var filterLongitude:String?= null

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapAutocompleteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Pick Location"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Places.initialize(applicationContext, "AIzaSyDAxB57qHvJsDFXNiJNc_bUufAgv3gkcWU")

        binding.etSearchLocation.isFocusable = false

        binding.etSearchLocation.setOnClickListener{
            val fieldList: List<Place.Field> = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME)
            val intent: Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(this)
            startActivityForResult(intent,100)
        }

        binding.btnFinish.setOnClickListener{


            val newIntent:Intent = Intent()
            newIntent.putExtra("eventLocation", "${eventLocality}")
            newIntent.putExtra("latitude","${filterLatitude.toString()}")
            newIntent.putExtra("longitude","${filterLongitude.toString()}")
            setResult(RESULT_OK, newIntent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK){
            val place: Place = Autocomplete.getPlaceFromIntent(data!!)


//                val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
//                val editor = sharedPreferences.edit()
//                editor.apply{
//                    putString("eventLocality",place.name)
//                }.commit()
            val latlong = place.latLng.toString().split(",")
            latitude = latlong[0]
            filterLatitude = latitude!!.drop(10)
            longitude = latlong[1]
            filterLongitude = longitude!!.replace(")","")
                Toast.makeText(this,"DATA SAVED",Toast.LENGTH_SHORT).show()

            eventLocality = place.name
            binding.etSearchLocation.setText(place.address)
            binding.tvLocation.setText(String.format("Locality Name :  %s", place.name))
        }else if ( resultCode == AutocompleteActivity.RESULT_ERROR){
            val status : Status = Autocomplete.getStatusFromIntent(data!!)
            Toast.makeText(applicationContext,status.statusMessage,Toast.LENGTH_SHORT).show()
        }

    }

}