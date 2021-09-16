package my.edu.tarc.okuappg11.activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityAddEventBinding
import my.edu.tarc.okuappg11.databinding.ActivityBookmarkBinding
import my.edu.tarc.okuappg11.models.Constants


class Bookmark : AppCompatActivity() {

    private lateinit var binding: ActivityBookmarkBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var bmArrayList: ArrayList<BookmarkArrayList>
    private lateinit var bmAdapter: BookmarkAdapter
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var userID: String? = null
    private var eventID: String? = null
    private lateinit var viewStubBookmark: ViewStub

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewStubBookmark = binding.viewStubBookmark
        viewStubBookmark.visibility = View.GONE

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userID = fAuth.currentUser!!.uid

        recyclerView = binding.recyclerViewBookmark
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.visibility = View.GONE

        bmArrayList = arrayListOf()
        bmAdapter = BookmarkAdapter(bmArrayList)

        recyclerView.adapter = bmAdapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Bookmarks"

        getData()
        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                if(bmArrayList.isEmpty()){
                    viewStubBookmark.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    viewStubBookmark.visibility = View.GONE
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
        if(bmArrayList.isEmpty()){
            viewStubBookmark.visibility = View.VISIBLE
        } else {
            viewStubBookmark.visibility = View.GONE
        }
    }

    private fun getData(){
        fStore = FirebaseFirestore.getInstance()
        fStore.collection("users")
            .document(userID!!)
            .collection("bookmarks")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    bmArrayList.clear()
                    value?.forEach{
                        fStore.collection("events").document(it.id.toString()).get()
                            .addOnSuccessListener {  dc ->
                                Log.d("CHECKoutside", dc.id)

                                val bmDetails = dc.toObject(BookmarkArrayList::class.java)
                                if(bmDetails != null){

                                    //bmArrayList.userID=userID!!
                                    bmDetails.eventID = dc.id
                                    bmDetails.eventName = dc.getString("eventName")
                                    bmDetails.startDate= dc.getString("startDate")
                                    bmDetails.startTime= dc.getString("startTime")
                                    bmDetails.location = dc.getString("eventLocation")
                                    bmDetails.eventThumbnailURL = dc.getString("eventThumbnailURL")
                                    bmArrayList.add(bmDetails)
                                    Log.d("CHECKINSIDE", dc.id)
                                    if(bmArrayList.isEmpty()){
                                        Log.d("try again","Array list is empty")
                                    } else {
                                        Log.d("Got array","Array list is not empty")
                                    }
                                    bmAdapter.notifyDataSetChanged()

                                    //Log.d("text", bmArrayList.userID)
                                }
                            }.addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)

                            }

                    }


                    bmAdapter.notifyDataSetChanged()

                    if(bmArrayList.isEmpty()){
                        Log.d("try again","Array list is empty")
                    } else {
                        Log.d("Got array","Array list is not empty")
                    }
                }
            })
    }

}