package my.edu.tarc.okuappg11.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.AutoScrollHelper
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.FragmentHomeBinding
import my.edu.tarc.okuappg11.models.TopicsAdapter
import my.edu.tarc.okuappg11.models.TopicsModel
import my.edu.tarc.okuappg11.models.TrendingAdapter
import my.edu.tarc.okuappg11.models.TrendingModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    private var trendingList: ArrayList<TrendingModel> = ArrayList()
    private var trendingListAdapter = TrendingAdapter(trendingList)
    private lateinit var recyclerViewTrending : RecyclerView

    private var topicList: ArrayList<TopicsModel> = ArrayList()
    private var topicListAdapter = TopicsAdapter(topicList)
    private lateinit var recyclerViewTopic : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun trendingListener(){
        fStore.collection("events")
        .orderBy("startDate", Query.Direction.ASCENDING).limit(5)
        .addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                trendingList.clear()
                value?.forEach {
                    val trendingDetails = it.toObject(TrendingModel::class.java)
                    if (trendingDetails != null) {
                        trendingDetails.eventID = it.id
                        trendingList.add(trendingDetails)
                        Log.d("text", trendingDetails.eventID)
                    }
                }
                trendingListAdapter.trendingList = trendingList
                trendingListAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun topicListener(){
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
                            topicDetails.storyID = it.id
                            topicList.add(topicDetails)
                            Log.d("text", topicDetails.storyID)
                        }
                    }
                    topicListAdapter.topicList = topicList
                    topicListAdapter.notifyDataSetChanged()
                }
            })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        recyclerViewTrending = binding.rvTrending
        recyclerViewTrending.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTrending.setHasFixedSize(true)

        trendingList = arrayListOf()
        trendingListAdapter = TrendingAdapter(trendingList)
        recyclerViewTrending.adapter= trendingListAdapter
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewTrending)

        recyclerViewTopic = binding.rvTopics
        recyclerViewTopic.layoutManager = LinearLayoutManager(this.context)
        recyclerViewTopic.setHasFixedSize(true)

        topicList = arrayListOf()
        topicListAdapter = TopicsAdapter(topicList)
        recyclerViewTopic.adapter= topicListAdapter

        trendingListener()
        topicListener()

        binding.btnLogout.setOnClickListener {
            activity?.finish()
            fAuth.signOut()
            findNavController().navigate(R.id.action_HomeFragment_to_signInFragment)
        }

        binding.btnSeeAll.setOnClickListener {

        }
    }
}



