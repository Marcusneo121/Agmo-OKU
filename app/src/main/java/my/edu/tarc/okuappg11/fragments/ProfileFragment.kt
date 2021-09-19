package my.edu.tarc.okuappg11.fragments

import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_profile.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.*
import my.edu.tarc.okuappg11.databinding.FragmentProfileBinding
import java.io.File

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var userRole: String? = null
    private var eventName:String? = null
    private var eventDescription:String? = null
    private var eventLocation:String? = null
    private var startDate:String? = null
    private var startTime:String? = null
    private lateinit var bmArrayList: ArrayList<BookmarkArrayList>
    private lateinit var bmAdapter: BookmarkAdapter
    private var userID: String? = null
    private var eventID: String? = null
    private var userName: String? = null
    private var userEmail: String? = null
    private lateinit var viewStubNoUpcomingEvent: ViewStub

    var constraintLayout: ConstraintLayout? = null
    var constraintSet: ConstraintSet? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var ueArrayList: ArrayList<AllUpcomingEventsArrayList>
    private lateinit var ueAdapter: AllUpcomingEventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        //layout_constraintTop_toTopOf="@+id/btnAllUpcomingEvents2"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getData(){
        fStore = FirebaseFirestore.getInstance()
        fStore.collection("users")
            .document(userID!!)
            .collection("upcomingEvents")
            .limit(2)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    ueArrayList.clear()
                    value?.forEach{
                        fStore.collection("events").document(it.id.toString()).get()
                            .addOnSuccessListener {  dc ->
                                Log.d("CHECKoutside", dc.id)

                                val ueDetails = dc.toObject(AllUpcomingEventsArrayList::class.java)
                                if(ueDetails != null){

                                    ueDetails.eventID = dc.id
                                    ueDetails.eventName = dc.getString("eventName")
                                    ueDetails.startDate= dc.getString("startDate")
                                    ueDetails.startTime= dc.getString("startTime")
                                    ueDetails.location = dc.getString("eventLocation")
                                    ueDetails.eventThumbnailURL = dc.getString("eventThumbnailURL")
                                    ueArrayList.add(ueDetails)
                                    Log.d("CHECKINSIDE", dc.id)
                                    if(ueArrayList.isEmpty()){
                                        Log.d("try again","Array list is empty")
                                    } else {
                                        Log.d("Got array","Array list is not empty")
                                    }
                                    ueAdapter.notifyDataSetChanged()

                                }
                            }.addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)

                            }
                    }

                    ueAdapter.notifyDataSetChanged()

                    if(ueArrayList.isEmpty()){
                        Log.d("try again","Array list is empty")
                    } else {
                        Log.d("Got array","Array list is not empty")
                    }
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fStore = FirebaseFirestore.getInstance()
        fAuth = FirebaseAuth.getInstance()
        userID = fAuth.currentUser?.uid

        viewStubNoUpcomingEvent = binding.viewStubNoUpcomingEvents
        viewStubNoUpcomingEvent.visibility = View.GONE

        btnMyPostedEvents.visibility = View.INVISIBLE
        btnMyVolunteers.visibility = View.INVISIBLE
        btnAllUpcomingEvents.visibility = View.INVISIBLE
        btnAllUpcomingEvents2.visibility = View.INVISIBLE

        recyclerView = binding.recyclerViewUpcomingEvents
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.visibility = View.GONE

        ueArrayList = arrayListOf()
        ueAdapter = AllUpcomingEventsAdapter(ueArrayList)
        recyclerView.adapter = ueAdapter

        getData()

        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                if(ueArrayList.isEmpty()){
                    viewStubNoUpcomingEvent.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    viewStubNoUpcomingEvent.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }, 1000)

        readImage(userID)

        fStore.collection("users").document(userID!!).get().addOnSuccessListener { it ->
            userRole = it.get("userType").toString()
            if(userRole == "Normal" ){
                btnMyPostedEvents.visibility = View.VISIBLE
                btnMyVolunteers.visibility = View.VISIBLE
                btnAllUpcomingEvents.visibility = View.INVISIBLE
                btnAllUpcomingEvents2.visibility = View.VISIBLE

                //binding.txtUpcomingEvent.translationY = 100.0f

                userName = it.get("name").toString()
                userEmail = it.get("email").toString()
                binding.tvUserType.text = "Normal User"
                binding.tvUserName.text = "$userName"
                binding.tvUserEmail.text = "$userEmail"


            } else {
                btnMyPostedEvents.visibility = View.INVISIBLE
                btnMyVolunteers.visibility = View.INVISIBLE
                btnAllUpcomingEvents.visibility = View.VISIBLE
                btnAllUpcomingEvents2.visibility = View.INVISIBLE

                binding.txtUpcomingEvent.translationY = -180.0f
                binding.recyclerViewUpcomingEvents.translationY = - 180.0f
                //binding.viewStubNoUpcomingEvents.translationY = -220.0f

                userName = it.get("name").toString()
                userEmail = it.get("email").toString()
                binding.tvUserType.text = "OKU User"
                binding.tvUserName.text = "$userName"
                binding.tvUserEmail.text = "$userEmail"

            }
        }.addOnFailureListener {
            Log.w(ContentValues.TAG,"UNABLE TO ADD ${it.suppressedExceptions}")
        }

        binding.btnBookmarks.setOnClickListener {
            val intent = Intent(activity, Bookmark::class.java)
            activity?.startActivity(intent)
        }

        binding.btnLikes.setOnClickListener {
            val intent = Intent(activity, Likes::class.java)
            activity?.startActivity(intent)
        }

        binding.btnAllUpcomingEvents.setOnClickListener {
            val intent = Intent(activity, AllUpcomingEvents::class.java)
            activity?.startActivity(intent)
        }

        binding.btnAllUpcomingEvents2.setOnClickListener {
            val intent = Intent(activity, AllUpcomingEvents::class.java)
            activity?.startActivity(intent)
        }

        binding.btnSetting.setOnClickListener {
            val intent = Intent(activity, SettingActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.btnMyVolunteers.setOnClickListener(){
            val intent = Intent(activity, MyVolunteerEventActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.btnMyPostedEvents.setOnClickListener(){
            val intent = Intent(activity, MyPostedEventActivity::class.java)
            activity?.startActivity(intent)
        }
    }

    private fun readImage(userID: String?){
        val sRef: StorageReference =
            FirebaseStorage.getInstance().reference.child("PROFILE_IMAGE${userID}.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        sRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.imgProfileFrag.setImageBitmap(bitmap)
            Log.d("CHECK", " IMAGE LOADED")
        }.addOnFailureListener {
            Glide.with(this).load(R.drawable.ic_round_account_circle_192).into(binding.imgProfileFrag)
            Log.d("CHECK", it.message.toString())
            Log.d("CHECK", "EVENT_THUMBNAIL${userID}.jpg")
        }
    }


    override fun onResume() {
        super.onResume()

        readImage(userID)

        if(ueArrayList.isEmpty()){
            viewStubNoUpcomingEvent.visibility = View.VISIBLE
        } else {
            viewStubNoUpcomingEvent.visibility = View.GONE
        }
    }
}