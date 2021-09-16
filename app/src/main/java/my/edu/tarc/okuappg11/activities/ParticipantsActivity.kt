package my.edu.tarc.okuappg11.activities

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


    private var eventId:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        eventId = intent.getStringExtra("EventUID")

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        recyclerView = binding.recyclerViewOKU
        recyclerView1  = binding.recyclerViewNormalUser
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView1.layoutManager = LinearLayoutManager(this)

        recyclerView.setHasFixedSize(true)
        recyclerView1 .setHasFixedSize(true)

        participantList = arrayListOf()
        participantOKUList = arrayListOf()
        participantsAdapter = ParticipantsAdapter(participantList)
        participantsOKUAdapter = ParticipantsOKUAdapter(participantOKUList)

        recyclerView.adapter = participantsOKUAdapter
        recyclerView1.adapter = participantsAdapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "All Participants"


        getNormal()

    }

    private fun getNormal() {

        Log.d("EVENTID", eventId.toString())
        fStore = FirebaseFirestore.getInstance()
        fStore.collection("events")
            .document(eventId.toString())
            .collection("participants")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    value?.forEach{ test ->
                        Log.d("Check1",test.id)
                        val participantDetails = test.toObject(ParticipantsOKUArrayList::class.java)

                        if(participantDetails != null){
                            fStore.collection("users").document("Daej5FnHv4PsqW7upLQtBINU90x2").get()
                                .addOnSuccessListener {  dc ->
                                    Log.d("Check2",dc.id)
                                        Log.d("Check3",dc.getString("userType").toString())
                                        Log.d("Check4",dc.getString("name").toString())
                                        participantDetails.name= dc.getString("name").toString()
                                        Log.d("Check5", participantDetails.name.toString())
                                        participantOKUList.add(participantDetails)


                                    Log.d("checkIn6",participantDetails.name)
                                    participantOKUList.add(participantDetails)

                                }
                            Log.d("checkOut7",participantDetails.name)



                        }
                    }


//                    for (dc: DocumentChange in value?.documentChanges!!) {
//                        if (dc.type == DocumentChange.Type.ADDED) {
//                            val volunteerDetails = dc.document.toObject( VolunteerRequestArrayList::class.java)
//                            volunteerDetails.vid = value.documents.toString()
//                            volunteerRequestArrayList.add(dc.document.toObject( VolunteerRequestArrayList::class.java))
//                        }
//                    }

                    participantsOKUAdapter.notifyDataSetChanged()

                    if(participantOKUList.isEmpty()){
                        Log.d("try again","Array list is empty")
                    } else {
                        Log.d("Got array","Array list is not empty")
                    }
                }
            })


    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getOKU() {
    }

}