package com.example.weatherapp

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.adapters.SearchRecycleAdapter
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.databinding.FragmentSearchBinding
import com.example.weatherapp.model.Location
import kotlinx.android.synthetic.main.search_toolbar.*
import kotlinx.coroutines.launch



class SearchFragment : Fragment() {

    //FragmentSearchBinding gets name from fragment's xml name
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

//    val myResponse: MutableLiveData<Location> = MutableLiveData()
//    val myResponses: MutableLiveData<List<Location>> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //activity?.title = getString(R.string.search)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root



        binding.searchButton.setOnClickListener {

            var searchQuery = if(binding.searchEdittext.text.toString() != "") binding.searchEdittext.text.toString() else "Zagreb"

            lifecycleScope.launch {

                //get all Locations from search
                val responses = RetrofitInstance.api.getLocations(searchQuery)

                //use woeids from found Locations to fill every location's consolidated_weather list of Weathers
                for (loc: Location in responses) {
                    var response = RetrofitInstance.api.getLocation(loc.woeid)
                    loc.consolidated_weather = response.consolidated_weather
                }

                binding.searchRecyclerView.adapter =
                    SearchRecycleAdapter(
                        responses
                    )
                binding.searchRecyclerView.layoutManager = LinearLayoutManager(activity)
                binding.searchRecyclerView.setHasFixedSize(true)

            }

        }



        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}