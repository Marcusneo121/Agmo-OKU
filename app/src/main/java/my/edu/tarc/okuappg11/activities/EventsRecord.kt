package my.edu.tarc.okuappg11.activities

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.recyclerview.EventCardArrayList
import java.io.File

class EventsRecord : AppCompatActivity() {
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var eventsRecordList: ArrayList<EventCardArrayList>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_record)
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        getData()

    }

    private fun getData() {
        Toast.makeText(this, "It runs here", Toast.LENGTH_LONG).show()
        fStore = FirebaseFirestore.getInstance()
        val collectionReference = fStore.collection("events")

        collectionReference.addSnapshotListener{ snapshot, e->
            if(e!=null){
                Toast.makeText(this, "$e", Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            if(snapshot != null){
                eventsRecordList = arrayListOf()
                val document = snapshot.documents
                document.forEach{
                    val eventDetails = it.toObject(EventCardArrayList::class.java)
                    if(eventDetails != null){
                        eventDetails.eventId=it.getString("eventId")
                        eventDetails.eventDescription= it.getString("eventDescription")
                        eventDetails.eventTitle = it.getString("eventName")
                        eventsRecordList.add(eventDetails)


                    }

                }
            }
        }
    }


}
