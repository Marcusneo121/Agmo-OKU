package my.edu.tarc.okuappg11.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewStub
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import my.edu.tarc.okuappg11.databinding.ActivityBookmarkBinding
import my.edu.tarc.okuappg11.databinding.ActivityLikesBinding


class Likes : AppCompatActivity() {
    private lateinit var binding: ActivityLikesBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var lkArrayList: ArrayList<LikesArrayList>
    private lateinit var lkAdapter: LikesAdapter
    private lateinit var fStore: FirebaseFirestore
    private lateinit var fAuth: FirebaseAuth
    private var userID: String? = null
    private var storyUID: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userID = fAuth.currentUser!!.uid


        recyclerView = binding.recyclerViewLikes
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        lkArrayList = arrayListOf()
        lkAdapter = LikesAdapter(lkArrayList)

        recyclerView.adapter = lkAdapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Likes"

        getData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getData() {
        fStore = FirebaseFirestore.getInstance()
        fStore.collection("users")
            .document(userID!!)
            .collection("likes")
            //.orderBy("storyCreatedDate", Query.Direction.DESCENDING).limit(5)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    lkArrayList.clear()
                    value?.forEach {
                        fStore.collection("stories").document(it.id.toString()).get()
                            .addOnSuccessListener {  dc ->
                                Log.d("CHECKoutside", dc.id)

                                val lkDetails = dc.toObject(LikesArrayList::class.java)
                                if(lkDetails != null){

                                    lkDetails.storyID = dc.id
                                    lkDetails.storyTitle = dc.getString("storyTitle").toString()
                                    lkDetails.storyThumbnailDescription= dc.getString("storyThumbnailDescription").toString()
                                    lkDetails.storyThumbnailURL = dc.getString("storyThumbnailURL").toString()
                                    lkArrayList.add(lkDetails)
                                    Log.d("CHECKINSIDE", dc.id)
                                    if(lkArrayList.isEmpty()){
                                        Log.d("try again","Array list is empty")
                                    } else {
                                        Log.d("Got array","Array list is not empty")
                                    }
                                    lkAdapter.notifyDataSetChanged()

                                }
                            }.addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)

                            }

                        }

                    lkAdapter.notifyDataSetChanged()

                    if (lkArrayList.isEmpty()) {
                        Log.d("try again", "Array list is empty")
                    } else {
                        Log.d("Got array", "Array list is not empty")
                    }
                }
            })
    }
}