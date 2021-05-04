package com.example.weatherapp

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.adapters.SearchRecycleAdapter
import com.example.weatherapp.databinding.FragmentSearchBinding
import kotlinx.android.synthetic.main.fragment_my_cities.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.loadingPanel


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
        //activity?.title = getString(R.string.search)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        //create adapter so updateData method can be used
        val adapter = SearchRecycleAdapter(sharedViewModel.locations, this, sharedViewModel, false)
        setAdapter(adapter)

        if (isNetworkConnected()) {
            //set viewmodel data
            sharedViewModel.retrieveLocations()

            //every time that viewmodel updates update adapter with current viewmodel's list of Locations
            sharedViewModel.locations.observe(viewLifecycleOwner, Observer {
                adapter.updateData(sharedViewModel.locations)
                if (!sharedViewModel.locations.value.isNullOrEmpty()) {
                    binding.loadingPanel.visibility = View.INVISIBLE
                    binding.recentLabel.visibility = View.VISIBLE
                }
            })



            binding.searchIcon.setOnClickListener {
                if (binding.searchInput.text.toString() != "") {
                    binding.loadingPanel.visibility = View.VISIBLE
                    val searchQuery = binding.searchInput.text.toString()
                    sharedViewModel.saveLocations(searchQuery)
                }
            }

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