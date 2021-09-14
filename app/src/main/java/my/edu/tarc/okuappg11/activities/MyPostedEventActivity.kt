package my.edu.tarc.okuappg11.activities

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import my.edu.tarc.okuappg11.data.PostedEventArrayList
import my.edu.tarc.okuappg11.databinding.ActivityMyPostedEventBinding
import my.edu.tarc.okuappg11.models.PostedEventAdapter

class MyPostedEventActivity : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private lateinit var binding: ActivityMyPostedEventBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var postedEventArrayList: ArrayList<PostedEventArrayList>
    private lateinit var postedEventAdapter: PostedEventAdapter
    private var userID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostedEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userID = fAuth.currentUser!!.uid

        recyclerView = binding.recyclerViewPostedEvent
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        postedEventArrayList = arrayListOf()
        postedEventAdapter = PostedEventAdapter(postedEventArrayList)

        recyclerView.adapter = postedEventAdapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Posted Event"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

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
            .collection("postedEvent")
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

                                val postedEventDetails = dc.toObject(PostedEventArrayList::class.java)
                                if(postedEventDetails != null){

                                    //bmArrayList.userID=userID!!
                                    postedEventDetails.eventId = dc.id
                                    postedEventDetails.eventName = dc.getString("eventName")
                                    postedEventDetails.startDate= dc.getString("startDate")
                                    postedEventDetails.startTime= dc.getString("startTime")
                                    postedEventDetails.location = dc.getString("eventLocation")
                                    postedEventDetails.eventStatus = dc.getString("status")
                                    postedEventDetails.eventThumbnailURL = dc.getString("eventThumbnailURL")
                                    postedEventArrayList.add(postedEventDetails)
                                    Log.d("CHECKINSIDE", dc.id)
                                    if(postedEventArrayList.isEmpty()){
                                        Log.d("try again","Array list is empty")
                                    } else {
                                        Log.d("Got array","Array list is not empty")
                                    }
                                    postedEventAdapter.notifyDataSetChanged()

                                    //Log.d("text", bmArrayList.userID)
                                }
                            }.addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)

                            }

                    }


                    postedEventAdapter.notifyDataSetChanged()

                    if(postedEventArrayList.isEmpty()){
                        Log.d("try again","Array list is empty")
                    } else {
                        Log.d("Got array","Array list is not empty")
                    }
                }
            })
    }

}