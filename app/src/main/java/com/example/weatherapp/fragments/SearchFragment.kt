package com.example.weatherapp.fragments

import android.R
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentSearchBinding


class SearchFragment : Fragment(), SearchRecycleAdapter.OnItemClickListener {

    //FragmentSearchBinding gets name from fragment's xml name
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root


        //adapter for suggestion list
        val adapterSuggestion = activity?.let { it1 ->
            ArrayAdapter(
                it1,
                R.layout.simple_list_item_1, sharedViewModel.suggestions.value!!
            )
        }
        binding.searchInput.setAdapter(adapterSuggestion)


        //create adapter so updateData method can be used
        val adapter = SearchRecycleAdapter(
            sharedViewModel.locations,
            this,
            sharedViewModel,
            false
        )
        setAdapter(adapter)


        //set up x icon
        binding.searchInput.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) binding.deleteInput.visibility = View.VISIBLE else binding.deleteInput.visibility = View.INVISIBLE
        }

        binding.deleteInput.setOnClickListener {
            binding.searchInput.text.clear()
        }

        if (isNetworkConnected()) {

            //if there is any data in viewmodel's suggestion list, reset the adapter of searchInput
            sharedViewModel.suggestions.observe(viewLifecycleOwner, Observer {
                if (!sharedViewModel.suggestions.value.isNullOrEmpty()) {
                    adapterSuggestion?.clear()
                    adapterSuggestion?.addAll(sharedViewModel.suggestions.value!!)
                    adapterSuggestion?.notifyDataSetChanged()
                    binding.searchInput.setAdapter(adapterSuggestion)
                }
            })

            //start network call for suggestion list only if more than one letter is typed in the searchInput
            binding.searchInput.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(binding.searchInput.text.length > 1)
                        sharedViewModel.getSearchSuggestionList(binding.searchInput.text.toString())
                }

            })

            binding.searchIcon.setOnClickListener {
                if (binding.searchInput.text.length > 2) {
                    binding.searchInput.error = null
                    binding.loadingPanel.visibility = View.VISIBLE
                    binding.loadingPanel.bringToFront()
                    val searchQuery = binding.searchInput.text.toString()
                    sharedViewModel.saveLocations(searchQuery)
                    binding.searchInput.text.clear()
                }
                else {
                    binding.searchInput.error = "Input must have at least 3 characters!"
                }
            }


            //set viewmodel locations
            sharedViewModel.retrieveLocations()

            //every time that viewmodel updates update adapter with current viewmodel's list of Locations
            sharedViewModel.locations.observe(viewLifecycleOwner, Observer {
                adapter.updateData(sharedViewModel.locations)
                if (!sharedViewModel.locations.value.isNullOrEmpty()) {
                    binding.loadingPanel.visibility = View.INVISIBLE
                    binding.recentLabel.visibility = View.VISIBLE
                }
            })


            binding.refreshLayout.setOnRefreshListener {
                sharedViewModel.retrieveLocations()
                //this is because of the case SearchFragment -> CityActivity -> set/unset isMyCity -> back button,
                //the search_locations table that this fragment uses isn't updated, only the my_cities table so this is the way to notify
                //this fragment that some location is set/unset as MyCity
                setAdapter(adapter)
                binding.refreshLayout.isRefreshing = false
            }





            // delete recent search, to be implemented in settings
//            binding.deleteButton.setOnClickListener {
//                sharedViewModel.deleteAll()
//                adapter.updateData(sharedViewModel.locations)
//            }

        }

        return view
    }

    private fun setAdapter(adapter: SearchRecycleAdapter) {
        //binding.searchRecyclerView.adapter = SearchRecycleAdapter(sharedViewModel.locations, this)
        binding.searchRecyclerView.adapter = adapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.searchRecyclerView.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int) {
//        val intent = Intent(context, RecyclerItemActivity::class.java)
//        startActivity(intent)
    }

    private fun isNetworkConnected(): Boolean {
        //add activity? if used in fragment
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //2
        val activeNetwork = connectivityManager.activeNetwork
        //3
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        //4
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


}