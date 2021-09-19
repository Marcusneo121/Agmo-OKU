package my.edu.tarc.okuappg11.activities

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityEventDetailsBinding
import my.edu.tarc.okuappg11.databinding.ActivitySeeAllCommentBinding
import my.edu.tarc.okuappg11.recyclerview.AllCommentsAdapter
import my.edu.tarc.okuappg11.recyclerview.AllCommentsArrayList

class SeeAllComment : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var binding: ActivitySeeAllCommentBinding
    private lateinit var allCommentsArrayList: ArrayList<AllCommentsArrayList>
    private lateinit var allCommentsAdapter: AllCommentsAdapter
    private lateinit var recyclerViewAllComment : RecyclerView
    private var userDisplayName:String?=null
    private var userProfileImageUri:String?= null
    private var eventID:String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeeAllCommentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        eventID = intent.getStringExtra("EventUID")



        recyclerViewAllComment = binding.rvSeeAllComment
        recyclerViewAllComment.layoutManager = LinearLayoutManager(this)
        recyclerViewAllComment.setHasFixedSize(true)
        allCommentsArrayList = arrayListOf()
        allCommentsAdapter = AllCommentsAdapter(allCommentsArrayList)

        recyclerViewAllComment.adapter = allCommentsAdapter

        supportActionBar?.title = "All Comments"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        readAllComments()

    }

    private fun readAllComments() {
        fStore = FirebaseFirestore.getInstance()
        fStore.collection("events")
            .document(eventID!!)
            .collection("comments")
            .orderBy("commentDate", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    allCommentsArrayList.clear()
                    value?.forEach {
                        Log.d("checkid",it.getString("userUID").toString())
                        fStore.collection("users").document(it.getString("userUID").toString()).get()
                            .addOnSuccessListener { dc ->

                                userDisplayName = dc.getString("name").toString()
                                userProfileImageUri = dc.getString("profileImageURL").toString()
                                Log.d("CHECKoutside", userProfileImageUri.toString())
                                val allUserCommentDetails = it.toObject(AllCommentsArrayList::class.java)

                                if (allUserCommentDetails != null){
                                    allUserCommentDetails.displayName = userDisplayName.toString()
                                    allUserCommentDetails.userImageUri = userProfileImageUri.toString()
                                    allUserCommentDetails.userID = it.getString("userUID").toString()
                                    allUserCommentDetails.commentDetails = it.getString("commentDetails").toString()
                                    allUserCommentDetails.commentDate = it.getString("commentDate").toString()
                                    allCommentsArrayList.add(allUserCommentDetails)

                                    allCommentsAdapter.notifyDataSetChanged()


                                }
                            }.addOnFailureListener {
                                Log.e("error", it.message.toString())
                            }
                        Log.d("CHECKout", userDisplayName.toString())


                        allCommentsAdapter.notifyDataSetChanged()

                        if (allCommentsArrayList.isEmpty()) {
                            Log.d("try again", "Array list is empty")
                        } else {
                            Log.d("Got array", "Array list is not empty")
                        }
                    }
                }
            })

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}