package my.edu.tarc.okuappg11.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewStub
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import my.edu.tarc.okuappg11.R
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
    private lateinit var viewStubPostedEvent: ViewStub

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostedEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewStubPostedEvent = binding.viewStubPostedEvent
        viewStubPostedEvent.visibility = View.GONE

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userID = fAuth.currentUser!!.uid

        recyclerView = binding.recyclerViewPostedEvent
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.visibility = View.GONE

        postedEventArrayList = arrayListOf()
        postedEventAdapter = PostedEventAdapter(postedEventArrayList)

        recyclerView.adapter = postedEventAdapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Posted Events"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        getData()
        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                if(postedEventArrayList.isEmpty()){
                    viewStubPostedEvent.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    viewStubPostedEvent.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }, 1000)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_posted_event, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_add_events -> {
                val intent = Intent(this, AddEventActivity::class.java)
                intent.putExtra("addedBy","eventorganizer")
                finish()
                startActivity(intent)
                return true
            }
            else -> {
            }
        }
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        if(postedEventArrayList.isEmpty()){
            viewStubPostedEvent.visibility = View.VISIBLE
        } else {
            viewStubPostedEvent.visibility = View.GONE
        }

        postedEventAdapter.notifyDataSetChanged()
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