package my.edu.tarc.okuappg11

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.renderscript.ScriptGroup
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.databinding.FragmentRegisterBinding
import my.edu.tarc.okuappg11.progressdialog.EmailSentDialog
import my.edu.tarc.okuappg11.progressdialog.RegisterDialog
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var userID: String? = null

    private lateinit var name: EditText
    private lateinit var password: EditText
    private lateinit var conPassword: EditText
    private lateinit var email: EditText
    private var userTypeName: String? = null

    private val dialogRegister = RegisterDialog(this)
    private val dialogEmailSent = EmailSentDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding
            .inflate(inflater, container, false)

        name = binding.etNameRegister
        password = binding.etPasswordRegister
        conPassword = binding.etConfirmPasswordRegister
        email = binding.etEmailRegister

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

//        name = binding.etNameRegister
//        password = binding.etPasswordRegister
//        conPassword = binding.etConfirmPasswordRegister
//        email = binding.etEmailRegister

        binding.btnRegister.setOnClickListener {

            val userTypeInput = binding.spinnerUserType.selectedItemPosition

            if (name.text.isEmpty()) {
                name.error = "Please enter full name."
                return@setOnClickListener
            }

            if (email.text.isEmpty()) {
                email.error = "Please enter email."
                return@setOnClickListener
            }

            if (password.text.isEmpty()) {
                password.error = "Please enter password."
                return@setOnClickListener
            }

            if (password.text.toString().length < 6) {
                password.error = "Password must more than 5 characters."
                return@setOnClickListener
            }

            if (!TextUtils.equals(password.text.toString(), conPassword.text.toString())) {
                conPassword.error = "Confirm password not match with password."
                return@setOnClickListener
            }

            if (userTypeInput == 0) {
                userTypeName = "OKU"
            } else {
                userTypeName = "Normal"
            }
            dialogRegister.startLoading()
            registerUser()

        }

        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_RegisterFragment_to_SignInFragment)
        }

        binding.btnLoginScreen.setOnClickListener {
            findNavController().navigate(R.id.action_RegisterFragment_to_SignInFragment)
        }
    }

    private fun registerUser() {

        val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        val strDate: String = dateFormat.format(date).toString()

        fAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //send email verification
                    val userEmailVerif: FirebaseUser? = fAuth.currentUser
                    userEmailVerif?.sendEmailVerification()?.addOnSuccessListener {
                        Toast.makeText(
                            this.context,
                            "Verification Email has been sent",
                            Toast.LENGTH_SHORT
                        ).show()
                    }?.addOnFailureListener {
                        Log.d("ExceptionMsg", "onFailure: Email not sent")
                    }

                    Toast.makeText(this.context, "User Created", Toast.LENGTH_SHORT).show()
                    userID = fAuth.currentUser?.uid
                    //val docRef: DocumentReference? = userID?.let { it1 -> fStore.collection("users").document(it1)}

                    val hashMapUser = hashMapOf(
                        "name" to name.text.toString(),
                        "email" to email.text.toString(),
                        "createdAt" to strDate,
                        "userType" to userTypeName.toString(),
                        //"userID" to userID,
                    )

                    fStore.collection("users").document(userID!!)
//                        .collection("userData")
                        .set(hashMapUser)
                        .addOnSuccessListener {
                            Log.d("TAG", "onSuccess: User profile is created for $userID")
                            dialogRegister.isDismiss()
                            dialogEmailSent.startLoading()
                            val handler = Handler()
                            handler.postDelayed(object : Runnable {
                                override fun run() {
                                    dialogEmailSent.isDismiss()
                                    findNavController().navigate(R.id.action_RegisterFragment_to_SignInFragment)
                                }
                            }, 3000)
                        }.addOnFailureListener {
                            Log.w(
                                ContentValues.TAG,
                                "Error adding document ${it.suppressedExceptions}"
                            )
                        }
                } else {
                    Toast.makeText(
                        this.context,
                        "Something went wrong. Try Again.",
                        Toast.LENGTH_LONG
                    ).show()
                    dialogRegister.isDismiss()
                }
            }.addOnFailureListener { e ->
                Log.d("Firebase",e.toString())
            Toast.makeText(this.context, "$e", Toast.LENGTH_SHORT).show()
        }
    }
}

