package my.edu.tarc.okuappg11.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityUpdateEventBinding
import my.edu.tarc.okuappg11.models.Constants
import my.edu.tarc.okuappg11.progressdialog.AddEventDialog
import my.edu.tarc.okuappg11.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class UpdateEvent : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateEventBinding
    private val dialogAddEvent = AddEventDialog(this)

    private var firestoreCheck: Boolean = false
    private var storageCheck: Boolean = false
    private var mSelectedImageFileUri: Uri? = null
    private var eventId: String? = null
    private var eventName: String? = null
    private var eventDescription: String? = null
    private var startDate: String? = null
    private var startTime: String? = null
    private var eventDuration: String? = null
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var eventOrganizer: String? = null
    private var eventOrganizerUID: String? = null
    private var eventLocation: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var eventThumbnailURL: String? = null
    private var updatedBy: String? = null
    private var currentUserID: String? = null
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        eventId = intent.getStringExtra("EventUID")
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        updatedBy = intent.getStringExtra("addedBy").toString()
        currentUserID = fAuth.currentUser!!.uid

        supportActionBar?.title = "Edit Event"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.textFieldUpdateEventOrganizerName.editText!!.isEnabled = false
        binding.textFieldUpdateDateStart.editText!!.isEnabled = false
        binding.textFieldUpdateTime.editText!!.isEnabled = false

        getData()
        readUserRole()

        binding.btnSelectDate.setOnClickListener {
            if (!binding.rbUpdateSingleDay.isChecked && !binding.rbUpdateMultiday.isChecked) {
                Toast.makeText(this, R.string.select_duration_event, Toast.LENGTH_LONG).show()

            } else {
                if (binding.rbUpdateSingleDay.isChecked) {
                    val datePicker =
                        MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Select date")
                            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                            .build()

                    datePicker.show(supportFragmentManager, "tag")


                    datePicker.addOnPositiveButtonClickListener {
                        eventDuration = "1"
                        binding.textFieldUpdateDateStart.editText?.setText(datePicker.headerText)
                    }
                }



                if (binding.rbUpdateMultiday.isChecked) {
                    val today: Long = MaterialDatePicker.todayInUtcMilliseconds();

                    val todayPair = Pair(today, today)


                    val dateRangePicker =
                        MaterialDatePicker.Builder.dateRangePicker()
                            .setTitleText("Select dates")
                            .build()

                    dateRangePicker.show(supportFragmentManager, "tag")
                    dateRangePicker.addOnPositiveButtonClickListener {
                        eventDuration = "2"
                        binding.textFieldUpdateDateStart.editText?.setText(dateRangePicker.headerText)
                    }
                }
            }
        }

        binding.btnSelectTime.setOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(10)
                    .setTitleText("Select Appointment time")
                    .build()

            picker.show(supportFragmentManager, "tag");

            picker.addOnPositiveButtonClickListener {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, picker.hour)
                cal.set(Calendar.MINUTE, picker.minute)
                val formattedDate = SimpleDateFormat("HH:mm").format(cal.time)
                binding.textFieldUpdateTime.editText?.setText(formattedDate)
            }

        }

        binding.ivUpdateEventThumbnail.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Toast.makeText(this,"You already have storage permission", Toast.LENGTH_SHORT).show()
                Constants.showImageChooser(this)


            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnPickPlace.setOnClickListener {
            //saveData()
            val intent = Intent(this@UpdateEvent, MapAutocompleteActivity::class.java)
            startActivityForResult(intent, 3)
        }


        binding.btnUpdate.setOnClickListener {
            if (validateEventDetails()) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.dialog_update_title))
                    .setMessage(resources.getString(R.string.dialog_update_description))
                    .setNegativeButton(resources.getString(R.string.dialog_update_negative)) { dialog, which ->
                        Toast.makeText(this, R.string.update_cancel, Toast.LENGTH_SHORT).show()
                    }
                    .setPositiveButton(resources.getString(R.string.dialog_update_positive)) { dialog, which ->
                        eventName = binding.textFieldUpdateEventName.editText!!.text.toString()
                        eventDescription =
                            binding.textFieldUpdateEventDescription.editText!!.text.toString()
                        startDate = binding.textFieldUpdateDateStart.editText!!.text.toString()
                        startTime = binding.textFieldUpdateTime.editText!!.text.toString()
                        val dateNow = Calendar.getInstance().time
                        val formattedDateNow = SimpleDateFormat("dd/MM/yyyy").format(dateNow)

                        Log.d("check", eventId.toString())
                        if (mSelectedImageFileUri != null) {

                            val sRef: StorageReference =
                                FirebaseStorage.getInstance().reference.child(
                                    Constants.USER_PROFILE_IMAGE + eventId + "."
                                            + Constants.getFileExtension(
                                        this,
                                        mSelectedImageFileUri
                                    )
                                )


                            sRef.putFile(mSelectedImageFileUri!!)
                                .addOnSuccessListener { taskSnapshot ->
                                    Log.e(
                                        "Firebase Image",
                                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                                    )
                                    dialogAddEvent.startLoading()

                                    taskSnapshot.metadata!!.reference!!.downloadUrl
                                        .addOnSuccessListener { uri ->
                                            Log.e("Downloadable Image URL", uri.toString())
                                            eventThumbnailURL = uri.toString()
                                            storageCheck = true
                                            val hashMapEvents = hashMapOf(
                                                "eventName" to eventName,
                                                "eventDescription" to eventDescription,
                                                "startDate" to startDate,
                                                "startTime" to startTime,
                                                "eventDuration" to eventDuration,
                                                "eventOrganizerName" to eventOrganizer,
                                                "eventOrganizerUID" to eventOrganizerUID,
                                                "eventLocation" to eventLocation,
                                                "eventCreatedDate" to formattedDateNow,
                                                "status" to "pending",
                                                "latitude" to latitude,
                                                "longitude" to longitude,
                                                "eventThumbnailURL" to eventThumbnailURL

                                            )
                                            val ref: DocumentReference =
                                                fStore.collection("events").document(eventId!!)

                                            ref.update(hashMapEvents as Map<String, Any>)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this,
                                                        R.string.update_success,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    Log.d(ContentValues.TAG, "Added Document")
                                                    firestoreCheck = true

                                                }
                                                .addOnFailureListener {
                                                    Log.w(
                                                        ContentValues.TAG,
                                                        "Error adding document ${it.suppressedExceptions}"
                                                    )
                                                }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e("ERROR", exception.message.toString())
                                        }
                                }
                        }


                        val handler = Handler()
                        handler.postDelayed(object : Runnable {
                            override fun run() {
                                //  val sharedPreferences:SharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
                                //  val editor = sharedPreferences.edit()
                                // editor.clear()
                                // editor.apply()
                                finish()

                                val intent1= Intent(this@UpdateEvent, HomeActivity::class.java)
                                val intent2 =
                                    Intent(this@UpdateEvent, AdminEventDetailsActivity::class.java)
                                if(userRole == "Normal"){
                                    intent1.putExtra("EventUID", "${eventId.toString()}")
                                    intent1.putExtra("updatedBy", userRole)
                                    startActivity(intent1)
                                } else {
                                    intent2.putExtra("EventUID", "${eventId.toString()}")
                                    intent2.putExtra("updatedBy", userRole)
                                    startActivity(intent2)
                                }
                                dialogAddEvent.isDismiss()

                            }
                        }, 5000)
                    }
                    .show()


            } else {
                if (binding.textFieldUpdateEventName.editText!!.text.isEmpty()) {
                    binding.textFieldUpdateEventName.editText!!.setError("This field cannot be empty")

                }

                if (binding.textFieldUpdateEventDescription.editText!!.text.isEmpty()) {
                    binding.textFieldUpdateEventDescription.editText!!.setError("This field cannot be empty")

                }

                if (binding.textFieldUpdateLocation.editText!!.text.isEmpty()) {
                    binding.textFieldUpdateLocation.editText!!.setError("Please select event location through the button.")
                }

                if (binding.textFieldUpdateDateStart.editText!!.text.isEmpty()) {
                    binding.textFieldUpdateDateStart.editText!!.setError("Please select the date through the button.")
                }

                if (binding.textFieldUpdateTime.editText!!.text.isEmpty()) {
                    binding.textFieldUpdateTime.editText!!.setError("Please select the time through the button.")
                }

            }


        }

    }

    private fun validateEventDetails(): Boolean {
        if (binding.textFieldUpdateEventName.editText!!.text.isEmpty() ||
            binding.textFieldUpdateEventDescription.editText!!.text.isEmpty() ||
            binding.textFieldUpdateDateStart.editText!!.text.isEmpty() ||
            binding.textFieldUpdateTime.editText!!.text.isEmpty()
        ) {
            return false
        } else {
            return true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun readUserRole() {
        fStore.collection("users").document(currentUserID!!).get()
            .addOnSuccessListener {
                userRole = it.getString("userType")
            }
    }

    private fun getData() {
        val docRef = fStore.collection("events").document(eventId!!)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    eventName = document.getString("eventName")
                    eventDescription = document.getString("eventDescription")
                    eventOrganizer = document.getString("eventOrganizerName")
                    eventDuration = document.getString("eventDuration")
                    eventOrganizerUID = document.getString("eventOrganizerUID")
                    startDate = document.getString("startDate")
                    startTime = document.getString("startTime")
                    eventLocation = document.getString("eventLocation")
                    latitude = document.getDouble("latitude")
                    longitude = document.getDouble("longitude")
                    eventThumbnailURL = document.getString("eventThumbnailURL")
                    binding.textFieldUpdateEventName.editText!!.setText(eventName)
                    binding.textFieldUpdateEventDescription.editText!!.setText(eventDescription)
                    binding.textFieldUpdateEventOrganizerName.editText!!.setText(eventOrganizer)

                    if (eventDuration == "1") {
                        binding.rbUpdateSingleDay.isChecked = true
                    } else {
                        binding.rbUpdateSingleDay.isChecked = true
                    }

                    binding.textFieldUpdateDateStart.editText!!.setText(startDate)
                    binding.textFieldUpdateTime.editText!!.setText(startTime)
                    binding.textFieldUpdateLocation.editText!!.setText(eventLocation)
                    GlideLoader(this).loadUserPicture(
                        Uri.parse(eventThumbnailURL)!!,
                        binding.ivUpdateEventThumbnail
                    )

                } else {
                    Log.d("HEY", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this,"Storage permission granted", Toast.LENGTH_LONG).show()
                Constants.showImageChooser(this)


            } else {
                //display another toast if permission not granted
                Toast.makeText(this, R.string.storage_permission_denied, Toast.LENGTH_LONG).show()

            }
        }


    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                if (data != null) {
                    try {
                        //uri of image
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(
                            mSelectedImageFileUri!!,
                            binding.ivUpdateEventThumbnail
                        )
                    } catch (e: Exception) {
                        Toast.makeText(this, R.string.image_selection_fail, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("REQUEST CANCELLED", "Image selection cancelled")
        }

        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                eventLocation = data!!.getStringExtra("eventLocation")
                latitude = data!!.getStringExtra("latitude")?.toDouble()
                longitude = data!!.getStringExtra("longitude")?.toDouble()
                binding.textFieldUpdateLocation.editText!!.setText(eventLocation)

            }
        }

    }

}