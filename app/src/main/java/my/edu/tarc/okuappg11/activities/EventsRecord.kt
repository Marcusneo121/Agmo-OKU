package my.edu.tarc.okuappg11.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.recyclerview.EventCardArrayList

class EventsRecord : AppCompatActivity() {
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var eventsRecordList: ArrayList<EventCardArrayList>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_record)
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()


    }

/*    private fun EventChangeListener(){
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        fStore.collection("events")
            .addSnapshotListener(object: EventListener<QuerySnapshot>){
                override fun onEvent(value:QuerySnapshot?,error: FirebaseFirestoreException){
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for(dc: DocumentChange in value?.documentChanges!!){
                        if ( dc.type == DocumentChange.Type.ADDED){
                                eventsRecordList.add(dc.document.toObject())
                            }
                    }

                }
        }



    }*/



}