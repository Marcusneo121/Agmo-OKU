package my.edu.tarc.okuappg11.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.MainActivity
import my.edu.tarc.okuappg11.activities.MyPostedEventActivity
import my.edu.tarc.okuappg11.databinding.FragmentAdminProfileBinding

class AdminProfileFragment : Fragment() {

    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var userID:String? = null
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
        _binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fStore = FirebaseFirestore.getInstance()
        fAuth = FirebaseAuth.getInstance()
        userID = fAuth.currentUser?.uid
        fStore.collection("users").document(userID!!).get().addOnSuccessListener { it ->

            userName = it.getString("name")
            userEmail = it.getString("email")

            binding.tvUserEmail.text = userEmail
            binding.tvUserName.text = userName
            binding.tvUserType.text = "Admin"

        }.addOnFailureListener {
            Log.e("error", it.message.toString())
        }

        binding.btnPostedEventsAdmin.setOnClickListener {
            val intent = Intent(activity, MyPostedEventActivity::class.java)
            activity?.startActivity(intent)
        }



        binding.btnLogoutAdmin2.setOnClickListener {
            val preferences = requireActivity().getSharedPreferences("sharedLogin", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            fAuth.signOut()
            activity?.finish()
            val i = Intent(this.context, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            //findNavController().navigate(R.id.action_adminProfileFragment_to_signInFragment2)
        }
    }

}