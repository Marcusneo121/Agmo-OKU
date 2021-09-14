package my.edu.tarc.okuappg11.activities

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityAddEventBinding
import my.edu.tarc.okuappg11.databinding.ActivityEventsRecordBinding
import my.edu.tarc.okuappg11.progressdialog.AddEventDialog
import my.edu.tarc.okuappg11.recyclerview.EventCardArrayList
import my.edu.tarc.okuappg11.recyclerview.EventsAdapter
import my.edu.tarc.okuappg11.utils.GlideLoader
import java.io.File

class EventsRecord : AppCompatActivity() {
    private lateinit var binding: ActivityEventsRecordBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsRecordList: ArrayList<EventCardArrayList>
    private lateinit var eventsRecordAdapter: EventsAdapter

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsRecordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()




        recyclerView = binding.rvEventsRecord
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        eventsRecordList = arrayListOf()
        eventsRecordList.clear()
        eventsRecordAdapter = EventsAdapter(eventsRecordList)

        recyclerView.adapter = eventsRecordAdapter

        //var adapter = VolunteerRequestAdapter(volunteerRequestArrayList)
        //recyclerView.adapter = adapter



        getData()


    }

    private fun getData(){

        fStore = FirebaseFirestore.getInstance()
        fStore.collection("events")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    eventsRecordList.clear()

                    value?.forEach{
                        val eventDetails = it.toObject(EventCardArrayList::class.java)
                        if(eventDetails != null){
                            Log.d("Check",eventDetails.eventTitle)
                            eventDetails.eventId = it.id
                            eventDetails.eventTitle = it.getString("eventName").toString()
                            eventDetails.imageUri = it.getString("eventThumbnailURL").toString()
                            eventsRecordList.add(eventDetails)
                            eventsRecordAdapter.notifyDataSetChanged()
                            recyclerView.invalidate()
                        }
                    }
//                    for (dc: DocumentChange in value?.documentChanges!!) {
//                        if (dc.type == DocumentChange.Type.ADDED) {
//                            val volunteerDetails = dc.document.toObject( VolunteerRequestArrayList::class.java)
//                            volunteerDetails.vid = value.documents.toString()
//                            volunteerRequestArrayList.add(dc.document.toObject( VolunteerRequestArrayList::class.java))
//                        }
//                    }
                    eventsRecordAdapter.notifyDataSetChanged()
                }
            })


        if(eventsRecordList.isEmpty()){
            Log.d("try again","Array list is empty")
        } else {
            Log.d("Got array","Array list is not empty")
        }

    }

    }



