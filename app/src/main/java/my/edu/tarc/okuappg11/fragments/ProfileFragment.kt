package my.edu.tarc.okuappg11.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.*
import my.edu.tarc.okuappg11.activities.*
import my.edu.tarc.okuappg11.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var userRole: String? = null
    private var eventName:String? = null
    private var eventDescription:String? = null
    private var eventLocation:String? = null
    private var startDate:String? = null
    private var startTime:String? = null
    private lateinit var bmArrayList: ArrayList<BookmarkArrayList>
    private lateinit var bmAdapter: BookmarkAdapter
    private var userID: String? = null
    private var eventID: String? = null
    private var userName: String? = null
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fStore = FirebaseFirestore.getInstance()
        fAuth = FirebaseAuth.getInstance()
        userID = fAuth.currentUser?.uid

        btnMyPostedEvents.visibility = View.INVISIBLE
        btnMyVolunteers.visibility = View.INVISIBLE
        btnMyEventHistory.visibility = View.INVISIBLE
        btnAllUpcomingEvents.visibility = View.INVISIBLE

        fStore.collection("users").document(userID!!).get().addOnSuccessListener { it ->
            userRole = it.get("userType").toString()
            if(userRole == "Normal" ){
                btnMyPostedEvents.visibility = View.VISIBLE
                btnMyVolunteers.visibility = View.VISIBLE
                btnMyEventHistory.visibility = View.INVISIBLE
                btnAllUpcomingEvents.visibility = View.INVISIBLE
                userName = it.get("name").toString()
                userEmail = it.get("email").toString()
                binding.tvUserType.text = "Normal User"
                binding.tvUserName.text = "$userName"
                binding.tvUserEmail.text = "$userEmail"


            } else {
                btnMyPostedEvents.visibility = View.INVISIBLE
                btnMyVolunteers.visibility = View.INVISIBLE
                btnMyEventHistory.visibility = View.VISIBLE
                btnAllUpcomingEvents.visibility = View.VISIBLE
                userName = it.get("name").toString()
                userEmail = it.get("email").toString()
                binding.tvUserType.text = "OKU User"
                binding.tvUserName.text = "$userName"
                binding.tvUserEmail.text = "$userEmail"

            }
        }.addOnFailureListener {
            Log.w(ContentValues.TAG,"UNABLE TO ADD ${it.suppressedExceptions}")
        }

        binding.btnBookmarks.setOnClickListener {
            val intent = Intent(activity, Bookmark::class.java)
            activity?.startActivity(intent)
        }

        binding.btnAddevent.setOnClickListener {
            val intent = Intent(activity, AddEventActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.btnAllUpcomingEvents.setOnClickListener {
            val intent = Intent(activity, AllUpcomingEvents::class.java)
            activity?.startActivity(intent)
        }
    }
}