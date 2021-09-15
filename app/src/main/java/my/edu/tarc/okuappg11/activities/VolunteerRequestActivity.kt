package my.edu.tarc.okuappg11.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import my.edu.tarc.okuappg11.databinding.ActivityVolunteerRequestBinding

class VolunteerRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVolunteerRequestBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var volunteerRequestArrayList: ArrayList<VolunteerRequestArrayList>
    private lateinit var volunteerRequestAdapter: VolunteerRequestAdapter

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerRequestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Volunteer Requests"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val eventId : String? = intent.getStringExtra("EventUID")
        Log.d("checkEvent", eventId.toString())


        recyclerView = binding.recyclerViewRequest
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        volunteerRequestArrayList = arrayListOf()
        volunteerRequestAdapter = VolunteerRequestAdapter(volunteerRequestArrayList)

        recyclerView.adapter = volunteerRequestAdapter

        //var adapter = VolunteerRequestAdapter(volunteerRequestArrayList)
        //recyclerView.adapter = adapter

        /*volunteerRequestAdapter.onItemClickListener(object: VolunteerRequestAdapter.onItemClickListener{
            override fun onItemClick(){
                val intent =  Intent(this@VolunteerRequestActivity, VolunteerRequestDetailActivity::class.java)
                intent.putExtra("EventUID","${eventId.toString()}")
                intent.putExtra("VolunteerID","${vid.toString()}")
                startActivity(intent)
            }

        })*/


        getData(eventId)


        /*val intent = Intent(this@VolunteerRequestActivity, VolunteerRequestDetailActivity::class.java)
        intent.putExtra("EventUID","${eventId.toString()}")
        startActivity(intent)*/


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getData(eventId: String?){

        fStore = FirebaseFirestore.getInstance()
        fStore.collection("events")
            .document(eventId.toString())
            .collection("volunteer")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    volunteerRequestArrayList.clear()
                    value?.forEach{
                        val volunteerDetails = it.toObject(VolunteerRequestArrayList::class.java)
                        if(volunteerDetails != null){
                            if(it.getString("vstatus").toString() == "Pending")
                            volunteerDetails.eventId=eventId.toString()
                            volunteerDetails.vid = it.id

                            volunteerRequestArrayList.add(volunteerDetails)

                            Log.d("text", volunteerDetails.eventId)
                        }
                        volunteerRequestArrayList.removeAll{
                            it.vstatus == "Accepted"
                        }
                    }


//                    for (dc: DocumentChange in value?.documentChanges!!) {
//                        if (dc.type == DocumentChange.Type.ADDED) {
//                            val volunteerDetails = dc.document.toObject( VolunteerRequestArrayList::class.java)
//                            volunteerDetails.vid = value.documents.toString()
//                            volunteerRequestArrayList.add(dc.document.toObject( VolunteerRequestArrayList::class.java))
//                        }
//                    }

                    volunteerRequestAdapter.notifyDataSetChanged()

                    if(volunteerRequestArrayList.isEmpty()){
                        Log.d("try again","Array list is empty")
                    } else {
                        Log.d("Got array","Array list is not empty")
                    }
                }
            })




    }
}