package my.edu.tarc.okuappg11.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import my.edu.tarc.okuappg11.databinding.FragmentSearchBinding
import my.edu.tarc.okuappg11.models.SearchListAdapter
import my.edu.tarc.okuappg11.models.SearchModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var fStore: FirebaseFirestore

    private var searchList: ArrayList<SearchModel> = ArrayList()
    private var searchListAdapter = SearchListAdapter(searchList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.etSearch.clearFocus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fStore = FirebaseFirestore.getInstance()

        searchList = arrayListOf()
        searchListAdapter = SearchListAdapter(searchList)

        binding.rvSearchResult.hasFixedSize()
        binding.rvSearchResult.layoutManager = LinearLayoutManager(this.context)
        binding.rvSearchResult.adapter = searchListAdapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText: String = binding.etSearch.text.toString()
                getInFirestoreEvents(searchText.capitalize())
                getInFirestorePlace(searchText.capitalize())
            }
        })
    }

    private fun getInFirestoreEvents(searchText: String) {
        fStore.collection("events")
            .orderBy("eventName")
            .startAt(searchText)
            .endAt("$searchText\uf8ff").limit(5)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    searchList.clear()
                    value?.forEach {
                        val searchDetails = it.toObject(SearchModel::class.java)
                        if (searchDetails != null) {
                            searchDetails.eventID = it.id
                            searchList.add(searchDetails)
                            Log.d("text", searchDetails.eventID)
                        }
                    }
//                    if (binding.etSearch.text.isEmpty()) {
//                        binding.rvSearchResult.visibility = View.INVISIBLE
//                    } else {
//                        binding.rvSearchResult.visibility = View.VISIBLE
//                        searchListAdapter.searchList = searchList
//                        searchListAdapter.notifyDataSetChanged()
//                    }
                }
            })
    }

    private fun getInFirestorePlace(searchText: String) {
        fStore.collection("events")
            .orderBy("eventLocation")
            .startAt(searchText)
            .endAt("$searchText\uf8ff").limit(5)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    value?.forEach {
                        val searchDetails = it.toObject(SearchModel::class.java)
                        if (searchDetails != null) {
                            searchDetails.eventID = it.id
                            searchList.add(searchDetails)
                            Log.d("text", searchDetails.eventID)
                        }
                    }
                    if (binding.etSearch.text.isEmpty()) {
                        binding.rvSearchResult.visibility = View.INVISIBLE
                    } else {
                        binding.rvSearchResult.visibility = View.VISIBLE
                        searchListAdapter.searchList = searchList
                        searchListAdapter.notifyDataSetChanged()
                    }
                }
            })
    }

    private fun searchInFirestore(searchText: String) {
        fStore.collection("events")
            .orderBy("eventName")
            .startAt(searchText)
            .endAt("$searchText\uf8ff").limit(5)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    searchList =
                        it.result!!.toObjects(SearchModel::class.java) as ArrayList<SearchModel>
                    if (binding.etSearch.text.isEmpty()) {
                        binding.rvSearchResult.visibility = View.INVISIBLE
                    } else {
                        binding.rvSearchResult.visibility = View.VISIBLE
                        searchListAdapter.searchList = searchList
                        searchListAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.d(TAG, "Error: ${it.exception!!.message}")
                }
            }
    }
}
