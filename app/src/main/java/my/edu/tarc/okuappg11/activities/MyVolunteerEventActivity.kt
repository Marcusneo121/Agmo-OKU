package my.edu.tarc.okuappg11.activities

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewStub
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import my.edu.tarc.okuappg11.data.VolunteerArrayList
import my.edu.tarc.okuappg11.databinding.ActivityMyVolunteerEventBinding
import my.edu.tarc.okuappg11.models.MyVolunteerEventAdapter

class MyVolunteerEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyVolunteerEventBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var volunteerArrayList: ArrayList<VolunteerArrayList>
    private lateinit var volunteerEventAdapter: MyVolunteerEventAdapter
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var userID: String? = null
    private var eventID: String? = null
    private lateinit var viewStubVolunteerEvent: ViewStub

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyVolunteerEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewStubVolunteerEvent = binding.viewStubVolunteerEvent
        viewStubVolunteerEvent.visibility = View.GONE

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userID = fAuth.currentUser!!.uid

        recyclerView = binding.recyclerViewVolunteerEvent
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.visibility = View.GONE

        volunteerArrayList = arrayListOf()
        volunteerEventAdapter = MyVolunteerEventAdapter(volunteerArrayList)

        recyclerView.adapter = volunteerEventAdapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Volunteer Event"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        getData()
        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                if(volunteerArrayList.isEmpty()){
                    viewStubVolunteerEvent .visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    viewStubVolunteerEvent .visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }, 1000)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        if(volunteerArrayList.isEmpty()){
            viewStubVolunteerEvent .visibility = View.VISIBLE
        } else {
            viewStubVolunteerEvent .visibility = View.GONE
        }
    }

    private fun getData(){
        fStore = FirebaseFirestore.getInstance()
        fStore.collection("users")
            .document(userID!!)
            .collection("volunteerEvent")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    value?.forEach{
                        fStore.collection("events").document(it.id.toString()).get()
                            .addOnSuccessListener {  dc ->
                                Log.d("CHECKoutside", dc.id)

                                val vEventDetails = dc.toObject(VolunteerArrayList::class.java)
                                if(vEventDetails != null){

                                    //bmArrayList.userID=userID!!
                                    vEventDetails.eventId = dc.id
                                    vEventDetails.eventName = dc.getString("eventName")
                                    vEventDetails.startDate= dc.getString("startDate")
                                    vEventDetails.startTime = dc.getString("startTime")
                                    vEventDetails.location = dc.getString("eventLocation")
                                    vEventDetails.eventThumbnailURL = dc.getString("eventThumbnailURL")
                                    volunteerArrayList.add(vEventDetails)
                                    Log.d("CHECKINSIDE", dc.id)
                                    if( volunteerArrayList.isEmpty()){
                                        Log.d("try again","Array list is empty")
                                    } else {
                                        Log.d("Got array","Array list is not empty")
                                    }
                                    volunteerEventAdapter.notifyDataSetChanged()

                                    //Log.d("text", bmArrayList.userID)
                                }
                            }.addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)

                            }

                    }


                    volunteerEventAdapter.notifyDataSetChanged()

                    if(volunteerArrayList.isEmpty()){
                        Log.d("try again","Array list is empty")
                    } else {
                        Log.d("Got array","Array list is not empty")
                    }
                }
            })

    }
}