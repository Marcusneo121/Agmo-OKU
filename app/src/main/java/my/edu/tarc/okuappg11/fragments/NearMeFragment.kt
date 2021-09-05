package my.edu.tarc.okuappg11.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.edu.tarc.okuappg11.databinding.FragmentNearMeBinding

class NearMeFragment : Fragment() {

    private var _binding: FragmentNearMeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNearMeBinding.inflate(inflater, container, false)
        return binding.root
    }
}