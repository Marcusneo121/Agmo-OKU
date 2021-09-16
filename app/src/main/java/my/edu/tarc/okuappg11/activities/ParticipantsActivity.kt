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
import my.edu.tarc.okuappg11.databinding.ActivityParticipantsBinding

class ParticipantsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParticipantsBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView1: RecyclerView

    private lateinit var eventUserListOKU: ArrayList<ParticipantsOKUArrayList>
    private lateinit var eventUserAdapterOKU: ParticipantsOKUAdapter

    private lateinit var eventUserListNormal: ArrayList<ParticipantsArrayList>
    private lateinit var eventUserAdapterNormal: ParticipantsAdapter

    private var totalParticipant: Int = 0


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
        recyclerView1 = binding.recyclerViewNormalUser
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView1.layoutManager = LinearLayoutManager(this)

        recyclerView.setHasFixedSize(true)
        recyclerView1.setHasFixedSize(true)


        eventUserListOKU = arrayListOf()
        eventUserListNormal = arrayListOf()
        eventUserAdapterOKU = ParticipantsOKUAdapter(eventUserListOKU)
        eventUserAdapterNormal = ParticipantsAdapter(eventUserListNormal)

        recyclerView.adapter = eventUserAdapterOKU
        recyclerView1.adapter = eventUserAdapterNormal
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "All Participants "


        getNormal()
        getOKU()
    }

    private fun getNormal() {

        fStore = FirebaseFirestore.getInstance()
        fStore.collection("events")
            .document(eventId!!)
            .collection("participants")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    eventUserListNormal.clear()
                    value?.forEach{
                        fStore.collection("users").document(it.id.toString()).get()
                            .addOnSuccessListener {  dc ->
                                Log.d("CHECKoutside", dc.id)

                                val participantDetails = dc.toObject(ParticipantsArrayList::class.java)
                                if(participantDetails != null){
                                    if (dc.getString("userType") == "Normal") {
                                        //bmArrayList.userID=userID!!
                                        participantDetails.participantName =
                                            dc.getString("name").toString()
                                        eventUserListNormal.add(participantDetails)
                                        Log.d("CHECKINSIDE", dc.id)

                                    }

                                    if(eventUserListNormal.isEmpty()){
                                        Log.d("try again","Array list is empty")
                                    } else {
                                        Log.d("Got array","Array list is not empty")
                                    }
                                    eventUserAdapterNormal.notifyDataSetChanged()

                                    //Log.d("text", bmArrayList.userID)
                                }
                            }.addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)

                            }

                    }


                    eventUserAdapterNormal.notifyDataSetChanged()

                    if(eventUserListNormal.isEmpty()){
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
        fStore = FirebaseFirestore.getInstance()
        fStore.collection("events")
            .document(eventId!!)
            .collection("participants")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    eventUserListOKU.clear()
                    value?.forEach{
                        fStore.collection("users").document(it.id.toString()).get()
                            .addOnSuccessListener {  dc ->
                                Log.d("CHECKoutside", dc.id)

                                val participantDetails = dc.toObject(ParticipantsOKUArrayList::class.java)
                                if(participantDetails != null){
                                    if (dc.getString("userType") == "OKU") {
                                        //bmArrayList.userID=userID!!
                                        participantDetails.participantName = dc.getString("name").toString()
                                        eventUserListOKU.add(participantDetails)
                                        Log.d("CHECKINSIDE", dc.id)

                                    }

                                    if(eventUserListOKU.isEmpty()){
                                        Log.d("try again","Array list is empty")
                                    } else {
                                        Log.d("Got array","Array list is not empty")
                                    }
                                    eventUserAdapterOKU.notifyDataSetChanged()

                                    //Log.d("text", bmArrayList.userID)
                                }
                            }.addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)

                            }

                    }


                    eventUserAdapterOKU.notifyDataSetChanged()

                    if(eventUserListOKU.isEmpty()){
                        Log.d("try again","Array list is empty")
                    } else {
                        Log.d("Got array","Array list is not empty")
                    }
                }
            })


    }



}