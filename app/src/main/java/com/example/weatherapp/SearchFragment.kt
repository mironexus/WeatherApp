package com.example.weatherapp

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.adapters.SearchRecycleAdapter
import com.example.weatherapp.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {

    //FragmentSearchBinding gets name from fragment's xml name
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //activity?.title = getString(R.string.search)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        //every time that viewmodel updates update adapter with current viewmodel's list of Locations
        sharedViewModel.locations.observe(viewLifecycleOwner, Observer {
            setAdapter()
        })


        binding.searchButton.setOnClickListener {

                if(binding.searchEdittext.text.toString() != "") {
                    var searchQuery = binding.searchEdittext.text.toString()

                    sharedViewModel.retrieveLocations(searchQuery)

                }

        }

        return view
    }

    private fun setAdapter() {
        binding.searchRecyclerView.adapter = SearchRecycleAdapter(sharedViewModel.locations)
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.searchRecyclerView.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}