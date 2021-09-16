package my.edu.tarc.okuappg11.activities

import android.content.ContentValues
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_profile.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityBookmarkBinding
import my.edu.tarc.okuappg11.databinding.ActivityParticipantsBinding
import my.edu.tarc.okuappg11.databinding.FragmentAdminHomeBinding
import my.edu.tarc.okuappg11.recyclerview.EventCardArrayList
import my.edu.tarc.okuappg11.recyclerview.EventsAdapter

class ParticipantsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParticipantsBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView1: RecyclerView

    private lateinit var participantList: ArrayList<ParticipantsArrayList>
    private lateinit var participantOKUList: ArrayList<ParticipantsOKUArrayList>
    private lateinit var participantsAdapter: ParticipantsAdapter
    private lateinit var participantsOKUAdapter : ParticipantsOKUAdapter

    private var userRole: String? = null
    private var eventId:String? = null
    private var userID: String? = null
    private var userName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        eventId = intent.getStringExtra("EventUID")

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        recyclerView = binding.recyclerViewOKU
        recyclerView1 = binding.recyclerViewNormalUser
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView1.layoutManager = LinearLayoutManager(this)

        recyclerView.setHasFixedSize(true)
        recyclerView1.setHasFixedSize(true)

        participantList = arrayListOf()
        participantOKUList = arrayListOf()
        participantsAdapter = ParticipantsAdapter(participantList)
        participantsOKUAdapter = ParticipantsOKUAdapter(participantOKUList)

        recyclerView.adapter = participantsOKUAdapter
        recyclerView1.adapter = participantsAdapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "All Participants"


        getData(userID)

    }

    private fun getData(userID: String?) {
        fStore.collection("users").document(userID!!).get().addOnSuccessListener { it ->
            userRole = it.get("userType").toString()
            if(userRole == "Normal" ){
                fStore = FirebaseFirestore.getInstance()
                fStore.collection("events")
                    .document(eventId.toString())
                    .collection("participants")
                    .addSnapshotListener(object : EventListener<QuerySnapshot> {
                        override fun onEvent(
                            value: QuerySnapshot?,
                            error: FirebaseFirestoreException?
                        ) {
                            if (error != null) {
                                Log.e("Firestore Error", error.message.toString())
                                return
                            }
                            participantList.clear()
                            value?.forEach { test ->
                                //Log.d("Check1",test.id)
                                val participantDetails =
                                    test.toObject(ParticipantsArrayList::class.java)

                                if (participantDetails != null) {
                                    fStore.collection("users").document(test.id.toString()).get()
                                        .addOnSuccessListener { dc ->
                                            Log.d("Check4", dc.getString("name").toString())
                                            participantDetails.name =
                                                dc.getString("name").toString()
                                            //participantDetails.name= dc.getString("userType").toString()
                                            participantList.add(participantDetails)
                                        }
                                }
                            }
                            participantsAdapter.notifyDataSetChanged()

                            if (participantList.isEmpty()) {
                                Log.d("try again (normal)", "Array list is empty")
                            } else {
                                Log.d("Got array (normal)", "Array list is not empty")
                            }
                        }
                    })

            } else {
                fStore = FirebaseFirestore.getInstance()
                fStore.collection("events")
                    .document(eventId.toString())
                    .collection("participants")
                    .addSnapshotListener(object : EventListener<QuerySnapshot> {
                        override fun onEvent(
                            value: QuerySnapshot?,
                            error: FirebaseFirestoreException?
                        ) {
                            if (error != null) {
                                Log.e("Firestore Error", error.message.toString())
                                return
                            }
                            participantOKUList.clear()
                            value?.forEach { test ->
                                //Log.d("Check1",test.id)
                                val participantOKUDetails =
                                    test.toObject(ParticipantsOKUArrayList::class.java)

                                if (participantOKUDetails != null) {
                                    fStore.collection("users").document(test.id.toString()).get()
                                        .addOnSuccessListener { dc ->
                                            Log.d("Check4", dc.getString("name").toString())
                                            participantOKUDetails.name =
                                                dc.getString("name").toString()
                                            //participantDetails.name= dc.getString("userType").toString()
                                            participantOKUList.add(participantOKUDetails)
                                        }
                                }
                            }
                            participantsOKUAdapter.notifyDataSetChanged()

                            if (participantOKUList.isEmpty()) {
                                Log.d("try again (OKU)", "Array list is empty")
                            } else {
                                Log.d("Got array (OKU)", "Array list is not empty")
                            }
                        }
                    })

            }
        }.addOnFailureListener {
            Log.w(ContentValues.TAG,"UNABLE TO ADD ${it.suppressedExceptions}")
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}