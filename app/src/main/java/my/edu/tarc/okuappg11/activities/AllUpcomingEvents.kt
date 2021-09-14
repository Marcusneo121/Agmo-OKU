package my.edu.tarc.okuappg11.activities

import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.databinding.ActivityAllUpcomingEventsBinding
import my.edu.tarc.okuappg11.databinding.ActivityBookmarkBinding
import java.io.File


class AllUpcomingEvents : AppCompatActivity() {
    private lateinit var binding: ActivityAllUpcomingEventsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var ueArrayList: ArrayList<AllUpcomingEventsArrayList>
    private lateinit var ueAdapter: AllUpcomingEventsAdapter
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var userID: String? = null
    private var eventID: String? = null
    //private var eventName:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllUpcomingEventsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val eventId = intent.getStringExtra("EventUID")
        userID = fAuth.currentUser!!.uid


        recyclerView = binding.recyclerViewAllUpcomingEvents
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        ueArrayList = arrayListOf()
        ueAdapter = AllUpcomingEventsAdapter(ueArrayList)

        recyclerView.adapter = ueAdapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Upcoming Events"

        getData()

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getData(){
        fStore = FirebaseFirestore.getInstance()
        fStore.collection("users")
            .document(userID!!)
            .collection("upcoming events")
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

}