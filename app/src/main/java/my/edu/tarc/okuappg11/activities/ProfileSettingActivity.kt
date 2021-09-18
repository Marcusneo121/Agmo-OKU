package my.edu.tarc.okuappg11.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityProfileSettingBinding
import my.edu.tarc.okuappg11.models.Constants
import my.edu.tarc.okuappg11.progressdialog.SavedDialog
import my.edu.tarc.okuappg11.progressdialog.SavingDialog
import my.edu.tarc.okuappg11.utils.GlideLoader
import java.io.File

class ProfileSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingBinding

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var userID: String? = null
    private var userName: String? = null
    private var userEmail: String? = null
    private var userRole: String? = null
    private var createdAt: String? = null
    private var userProfilePic: String? = null
    private var emailReset: String? = null
    private val dialogSaving = SavingDialog(this)
    private val dialogSaved = SavedDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnSaveProfileSettings.visibility = View.GONE
        binding.btnSaveProfileSettings.isClickable = false
        binding.btnSaveProfileSettings.isEnabled = false
        binding.btnSaveProfileSettingsDisabled.visibility = View.VISIBLE
        binding.btnSaveProfileSettingsDisabled.isClickable = true
        binding.btnSaveProfileSettingsDisabled.isEnabled = true

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        userID = fAuth.currentUser!!.uid

        supportActionBar?.title = "Profile Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff262626.toInt()))

        readData(userID)

        binding.btnResetPasswordProfile.setOnClickListener {
            val intent= Intent(this@ProfileSettingActivity, ForgotPassProfile::class.java)
            intent.putExtra("emailReset", emailReset)
            startActivity(intent)
        }

        binding.imgProfile.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ){
                //Toast.makeText(this,"You already have storage permission", Toast.LENGTH_SHORT).show()
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        val ref: DocumentReference = fStore.collection("users").document(userID!!.toString())

        binding.btnSaveProfileSettings.setOnClickListener {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                Constants.USER_IMAGE + userID + ".jpg")

            dialogSaving.startLoading()
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                Log.e("Firebase Image", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    val dataProfile = hashMapOf(
                        "name" to userName.toString(),
                        "email" to userEmail.toString(),
                        "createdAt" to createdAt.toString(),
                        "userType" to userRole.toString(),
                        "profileImageURL" to uri.toString()
                    )
                    ref.set(dataProfile, SetOptions.merge())
                        .addOnSuccessListener {
                            //addPostedEvent()
                            Toast.makeText(this,R.string.profile_success,Toast.LENGTH_SHORT).show()
                            Log.d(ContentValues.TAG, "Added Document")
                            dialogSaving.isDismiss()
                            dialogSaved.startLoading()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this,R.string.profile_failed,Toast.LENGTH_SHORT).show()
                            dialogSaving.isDismiss()
                            Log.w(
                                ContentValues.TAG,
                                "Error adding document ${it.suppressedExceptions}"
                            )
                        }
                }
            }
            binding.btnSaveProfileSettings.visibility = View.GONE
            binding.btnSaveProfileSettings.isClickable = false
            binding.btnSaveProfileSettings.isEnabled = false
            binding.btnSaveProfileSettingsDisabled.visibility = View.VISIBLE
            binding.btnSaveProfileSettingsDisabled.isClickable = true
            binding.btnSaveProfileSettingsDisabled.isEnabled = true
        }
    }

    private fun readData(userID: String?) {
        val profileRef = fStore.collection("users").document(userID.toString())
        profileRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    createdAt = document.getString("createdAt")
                    userEmail = document.getString("email")
                    userName = document.getString("name")
                    userRole = document.getString("userType")
                    //userProfilePic = document.getString("profilePic")

                    binding.tvEmailProfile.text = userEmail
                    binding.tvUsername.text = userName

                    emailReset = userEmail

                    if(userRole == "Normal"){
                        binding.lblUserType.text = "${userRole} user"
                    } else {
                        binding.lblUserType.text = userRole
                    }
                } else {
                    Log.d("HEY", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        val sRef: StorageReference =
            FirebaseStorage.getInstance().reference.child("PROFILE_IMAGE${userID}.jpg")

//        if(userProfilePic == null){
//            Glide.with(this).load(R.drawable.ic_round_account_circle_24).into(binding.imgProfile)
//        } else {
//            GlideLoader(this).loadUserPicture(Uri.parse(userProfilePic),binding.imgProfile)
//        }

        val localfile = File.createTempFile("tempImage", "jpg")
        sRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.imgProfile.setImageBitmap(bitmap)
            Log.d("CHECK", " IMAGE LOADED")
        }.addOnFailureListener {
            Glide.with(this).load(R.drawable.ic_round_account_circle_192).into(binding.imgProfile)
            Log.d("CHECK", it.message.toString())
            Log.d("CHECK", "EVENT_THUMBNAIL${userID}.jpg")
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
            if (resultCode== Activity.RESULT_OK){
                if (data !=null){
                    try{
                        //uri of image
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.imgProfile)
                        binding.btnSaveProfileSettings.visibility = View.VISIBLE
                        binding.btnSaveProfileSettings.isClickable = true
                        binding.btnSaveProfileSettings.isEnabled = true
                        binding.btnSaveProfileSettingsDisabled.visibility = View.GONE
                        binding.btnSaveProfileSettingsDisabled.isClickable = false
                        binding.btnSaveProfileSettingsDisabled.isEnabled = false
                    }catch(e: Exception){
                        Toast.makeText(this, R.string.image_selection_fail, Toast.LENGTH_LONG).show()
                        binding.btnSaveProfileSettings.visibility = View.GONE
                        binding.btnSaveProfileSettings.isClickable = false
                        binding.btnSaveProfileSettings.isEnabled = false
                        binding.btnSaveProfileSettingsDisabled.visibility = View.VISIBLE
                        binding.btnSaveProfileSettingsDisabled.isClickable = true
                        binding.btnSaveProfileSettingsDisabled.isEnabled = true
                    }
                }
            }
        }else if (resultCode == Activity.RESULT_CANCELED){
            Log.e("REQUEST CANCELLED", "Image selection cancelled")
        }

//        if(requestCode == 3){
//            if(resultCode == RESULT_OK){
//                eventLocation = data!!.getStringExtra("eventLocation")
//                latitude = data!!.getStringExtra("latitude")?.toDouble()
//                longitude = data!!.getStringExtra("longitude")?.toDouble()
//                binding.textFieldLocation.editText!!.setText(eventLocation)
//
//            }
//        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}