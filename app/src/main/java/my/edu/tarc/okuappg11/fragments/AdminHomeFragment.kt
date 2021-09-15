package my.edu.tarc.okuappg11.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_admin_home.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.AddEventActivity
import my.edu.tarc.okuappg11.activities.AddStoryActivity
import my.edu.tarc.okuappg11.activities.AdminPendingEvents
import my.edu.tarc.okuappg11.databinding.FragmentAdminHomeBinding
import my.edu.tarc.okuappg11.models.TopicsAdapter
import my.edu.tarc.okuappg11.models.TopicsModel
import my.edu.tarc.okuappg11.models.TrendingAdapter
import my.edu.tarc.okuappg11.models.TrendingModel
import my.edu.tarc.okuappg11.recyclerview.EventCardArrayList
import my.edu.tarc.okuappg11.recyclerview.EventsAdapter


class AdminHomeFragment : Fragment() {

    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView1: RecyclerView

    private lateinit var eventsRecordList: ArrayList<EventCardArrayList>
    private lateinit var eventsRecordAdapter: EventsAdapter

    private var topicList: ArrayList<TopicsModel> = ArrayList()
    private var topicListAdapter = TopicsAdapter(topicList)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        recyclerView = binding.rvAdminEventsRecord
        recyclerView1 = binding.rvAdminEventsRecord
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)

        eventsRecordList = arrayListOf()
        eventsRecordList.clear()
        eventsRecordAdapter = EventsAdapter(eventsRecordList)

        topicList = arrayListOf()
        topicListAdapter = TopicsAdapter(topicList)




        recyclerView.adapter = eventsRecordAdapter
        getData()
        val displayArray = listOf("All Events","Pending Events","Approved Events","Rejected Events", "Stories")
        val userTypeInput = binding.spinnerSelectionDisplay.selectedItemPosition
        val spinAdapter = ArrayAdapter(this.requireContext() ,android.R.layout.simple_list_item_1,displayArray)
        binding.spinnerSelectionDisplay.adapter = spinAdapter

        binding.spinnerSelectionDisplay.onItemSelectedListener = object :
           AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long){
                    if(binding.spinnerSelectionDisplay.selectedItemPosition == 0){
                        recyclerView.adapter = eventsRecordAdapter
                        getData()
                    }else if (binding.spinnerSelectionDisplay.selectedItemPosition == 1){
                        recyclerView.adapter = eventsRecordAdapter
                        getPendingEvent()
                    }else if (binding.spinnerSelectionDisplay.selectedItemPosition == 2){
                        recyclerView.adapter = eventsRecordAdapter
                        getApprovedEvent()
                    }else if (binding.spinnerSelectionDisplay.selectedItemPosition == 3){
                        recyclerView.adapter = eventsRecordAdapter
                        getRejectedEvent()
                    }else if (binding.spinnerSelectionDisplay.selectedItemPosition == 4){
                        recyclerView.adapter = topicListAdapter

                        Log.d("CHECK","STORIES")
                        getStories()
                    }
                }


            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }




        }


        //var adapter = VolunteerRequestAdapter(volunteerRequestArrayList)
        //recyclerView.adapter = adapter





        btnAdd.setOnClickListener {
            val addSelection = arrayOf("Event","Story")
            val intent = Intent(this@AdminHomeFragment.context, AddEventActivity::class.java)
            val intent1 = Intent(this@AdminHomeFragment.context, AddStoryActivity::class.java)

            MaterialAlertDialogBuilder(it.context)
                .setTitle("Add")
                .setItems(addSelection){dialog, which ->
                    when (which) {
                        0 -> startActivity(intent)
                        1 -> startActivity(intent1)


                    }
                }.show()
        }

        btnNotification.setOnClickListener {
            val intent = Intent(this@AdminHomeFragment.context, AdminPendingEvents::class.java)
            startActivity(intent)
        }
    }

    private fun getStories() {
        fStore.collection("stories")
            .orderBy("storyCreatedDate", Query.Direction.DESCENDING).limit(5)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    topicList.clear()
                    value?.forEach {
                        val topicDetails = it.toObject(TopicsModel::class.java)
                        if (topicDetails != null) {
                            topicDetails.accessBy = "admin"
                            topicDetails.storyID = it.id
                            topicDetails.storyThumbnailDescription = it.getString("storyThumbnailDescription").toString()
                            topicDetails.storyTitle= it.getString("storyTitle").toString()
                            topicList.add(topicDetails)
                            Log.d("text", topicDetails.storyID)
                        }
                    }
                    topicListAdapter.topicList = topicList
                    topicListAdapter.notifyDataSetChanged()
                }
            })
    }

    private fun getRejectedEvent() {
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
                            if (it.getString("status").toString() == "rejected") {
                                Log.d("Check", eventDetails.eventTitle)
                                eventDetails.eventId = it.id
                                eventDetails.eventTitle = it.getString("eventName").toString()
                                eventDetails.imageUri = it.getString("eventThumbnailURL").toString()
                                eventDetails.eventTime = it.getString("startTime").toString()
                                eventDetails.eventLocation = it.getString("eventLocation").toString()
                                eventDetails.eventDate = it.getString("startDate").toString()
                                eventDetails.eventStatus= it.getString("status").toString()
                                eventsRecordList.add(eventDetails)
                                eventsRecordAdapter.notifyDataSetChanged()
                                recyclerView.invalidate()
                            }
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

    private fun getApprovedEvent() {
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
                            if (it.getString("status").toString() == "accepted") {
                                Log.d("Check", eventDetails.eventTitle)
                                eventDetails.eventId = it.id
                                eventDetails.eventTitle = it.getString("eventName").toString()
                                eventDetails.imageUri = it.getString("eventThumbnailURL").toString()
                                eventDetails.eventTime = it.getString("startTime").toString()
                                eventDetails.eventLocation = it.getString("eventLocation").toString()
                                eventDetails.eventDate = it.getString("startDate").toString()
                                eventDetails.eventStatus= it.getString("status").toString()

                                eventsRecordList.add(eventDetails)
                                eventsRecordAdapter.notifyDataSetChanged()
                                recyclerView.invalidate()
                            }
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

    private fun getPendingEvent() {
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
                            if (it.getString("status").toString() == "pending") {
                                Log.d("Check", eventDetails.eventTitle)
                                eventDetails.eventId = it.id
                                eventDetails.eventTitle = it.getString("eventName").toString()
                                eventDetails.imageUri = it.getString("eventThumbnailURL").toString()
                                eventDetails.eventTime = it.getString("startTime").toString()
                                eventDetails.eventLocation = it.getString("eventLocation").toString()
                                eventDetails.eventDate = it.getString("startDate").toString()
                                eventDetails.eventStatus= it.getString("status").toString()

                                eventsRecordList.add(eventDetails)
                                eventsRecordAdapter.notifyDataSetChanged()
                                recyclerView.invalidate()
                            }
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

    private fun getData() {
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
                            eventDetails.eventTime = it.getString("startTime").toString()
                            eventDetails.eventLocation = it.getString("eventLocation").toString()
                            eventDetails.eventDate = it.getString("startDate").toString()
                            eventDetails.eventStatus= it.getString("status").toString()

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
