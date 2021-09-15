package my.edu.tarc.okuappg11.activities

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import my.edu.tarc.okuappg11.databinding.ActivityAllTopicsBinding
import my.edu.tarc.okuappg11.models.TopicsAdapter
import my.edu.tarc.okuappg11.models.TopicsModel

class AllTopicsActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    private lateinit var binding: ActivityAllTopicsBinding
    private var allTopicList: ArrayList<TopicsModel> = ArrayList()
    private var allTopicListAdapter = TopicsAdapter(allTopicList)
    private lateinit var recyclerViewAllTopic : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllTopicsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Story & Article"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff262626.toInt()))

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        recyclerViewAllTopic = binding.rvAllTopics
        recyclerViewAllTopic.layoutManager = LinearLayoutManager(this)
        recyclerViewAllTopic.setHasFixedSize(true)

        allTopicList = arrayListOf()
        allTopicListAdapter = TopicsAdapter(allTopicList)
        recyclerViewAllTopic.adapter= allTopicListAdapter

        allTopicListener()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun allTopicListener(){
        fStore.collection("stories")
            .orderBy("storyCreatedDate", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    allTopicList.clear()
                    value?.forEach {
                        val allTopicDetails = it.toObject(TopicsModel::class.java)
                        if (allTopicDetails != null) {
                            allTopicDetails.storyID = it.id
                            allTopicList.add(allTopicDetails)
                            Log.d("text", allTopicDetails.storyID)
                        }
                    }
                    allTopicListAdapter.topicList = allTopicList
                    allTopicListAdapter.notifyDataSetChanged()
                }
            })
    }
}