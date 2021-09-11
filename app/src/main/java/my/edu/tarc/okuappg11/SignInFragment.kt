package my.edu.tarc.okuappg11

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.activities.AddEventActivity
import my.edu.tarc.okuappg11.activities.AdminHomeActivity
import my.edu.tarc.okuappg11.activities.HomeActivity
import my.edu.tarc.okuappg11.activities.MapAutocompleteActivity
import my.edu.tarc.okuappg11.databinding.FragmentSigninBinding
import my.edu.tarc.okuappg11.progressdialog.EmailVerifyDialog
import my.edu.tarc.okuappg11.progressdialog.SignInDialog
import my.edu.tarc.okuappg11.progressdialog.SignInErrorDialog

class SignInFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var userID: String? = null
    private var userRole: String? = null

    private val dialogSignIn = SignInDialog(this)
    private val dialogEmailNotVerified = EmailVerifyDialog(this)
    private val dialogSignInError = SignInErrorDialog(this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun finish(context: Context?) {
        finish(context)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {

                    Toast.makeText(context, "Press Again to Exit", Toast.LENGTH_SHORT).show()
                    val handler = Handler()
                    handler.postDelayed(object: Runnable{
                        override fun run() {
                            finish(context)
                        }
                    }, 1000)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

//    override fun onResume() {
//        super.onResume()
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val password = binding.etPassword
        val email = binding.etEmail


        binding.btnLogin.setOnClickListener {
            if(email.text.isEmpty()){
                email.error = "Please enter email."
                return@setOnClickListener
            }

            if(password.text.isEmpty()){
                password.error = "Please enter password."
                return@setOnClickListener
            }

            if(password.text.toString().length < 6){
                password.error = "Password must more than 5 characters."
            }

            dialogSignIn.startLoading()

            fAuth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        if(fAuth.currentUser?.isEmailVerified == true){
                            userID = fAuth.currentUser?.uid
                            fStore.collection("users").document(userID!!).get().addOnSuccessListener { it ->
                                userRole = it.get("userType").toString()
                                if(userRole == "OKU" || userRole == "Normal"){

                                    val intent = Intent(activity, HomeActivity::class.java)
                                    activity?.startActivity(intent)
                                    dialogSignIn.isDismiss()
                                    Toast.makeText(this.context, "Logged In", Toast.LENGTH_SHORT).show()

                                } else if (userRole == "Admin"){

                                    val intent = Intent(activity, AdminHomeActivity::class.java)
                                    activity?.startActivity(intent)
                                    dialogSignIn.isDismiss()
                                    Toast.makeText(this.context, "Admin Logged In", Toast.LENGTH_SHORT).show()

                                } else {
                                    dialogSignIn.isDismiss()
                                    dialogSignInError.startLoading()

                                    val handler = Handler()
                                    handler.postDelayed(object: Runnable{
                                        override fun run() {
                                            dialogSignInError.isDismiss()
                                        }
                                    }, 3000)
                                }
                            }.addOnFailureListener {
                                dialogSignIn.isDismiss()
                                dialogSignInError.startLoading()

                                val handler = Handler()
                                handler.postDelayed(object: Runnable{
                                    override fun run() {
                                        dialogSignInError.isDismiss()
                                    }
                                }, 3000)
                            }
                        } else {
                            dialogSignIn.isDismiss()
                            dialogEmailNotVerified.startLoading()

                            val handler = Handler()
                            handler.postDelayed(object: Runnable{
                                override fun run() {
                                    dialogEmailNotVerified.isDismiss()
                                }
                            }, 3000)
                        }
                    } else {
                        dialogSignIn.isDismiss()
                        dialogSignInError.startLoading()

                        val handler = Handler()
                        handler.postDelayed(object: Runnable{
                            override fun run() {
                                dialogSignInError.isDismiss()
                            }
                        }, 3000)
                    }
                }.addOnFailureListener { e->
                    dialogSignIn.isDismiss()
                    dialogSignInError.startLoading()

                    val handler = Handler()
                    handler.postDelayed(object: Runnable{
                        override fun run() {
                            dialogSignInError.isDismiss()
                        }
                    }, 3000)
                    //Toast.makeText(this.context, "$e", Toast.LENGTH_LONG).show()
                }
            //findNavController().navigate(R.id.action_SignInFragment_to_homeActivity)
        }

        binding.btnRegisterScreen.setOnClickListener {
            findNavController().navigate(R.id.action_SignInFragment_to_RegisterFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}