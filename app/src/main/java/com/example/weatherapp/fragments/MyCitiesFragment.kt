package com.example.weatherapp.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMyCitiesBinding
import java.util.*


class MyCitiesFragment : Fragment(), SearchRecycleAdapter.OnItemClickListener {

    private var _binding: FragmentMyCitiesBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCitiesBinding.inflate(inflater, container, false)
        val view = binding.root


        val adapter = SearchRecycleAdapter(
            sharedViewModel.myCities,
            this,
            sharedViewModel,
            true
        )
        setAdapter(adapter)

        var editMode = false

        binding.editIcon.setOnClickListener {

            if(!editMode) {
                itemTouchHelper.attachToRecyclerView(binding.searchRecyclerView)
                binding.editIcon.setImageResource(R.drawable.ic_done)
                binding.calendarIcon.visibility = View.GONE
                editMode = true
            }
            else {
                itemTouchHelper.attachToRecyclerView(null)
                binding.editIcon.setImageResource(R.drawable.ic_edit)
                binding.calendarIcon.visibility = View.VISIBLE
                editMode = false
            }

            adapter.setEditable(editMode)
            setAdapter(adapter)

        }


        if (isNetworkConnected()) {
            sharedViewModel.retrieveMyCities()

            sharedViewModel.myCities.observe(viewLifecycleOwner, Observer {
                adapter.updateData(sharedViewModel.myCities)
            })

            binding.refreshLayout.setOnRefreshListener {
                sharedViewModel.retrieveMyCities()
                binding.refreshLayout.isRefreshing = false
            }

        }


        return view
    }


    private fun setAdapter(adapter: SearchRecycleAdapter) {
        //binding.searchRecyclerView.adapter = SearchRecycleAdapter(sharedViewModel.locations, this)
        binding.searchRecyclerView.adapter = adapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.searchRecyclerView.setHasFixedSize(true)
    }


    //for rearrange
    private val itemTouchHelper by lazy {

        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, 0) {

                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {

                    val adapter = recyclerView.adapter as SearchRecycleAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition

                    Collections.swap(sharedViewModel.myCities.value, from, to)

                    adapter.notifyItemMoved(from, to)

                    return true
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                }

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?,
                                               actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(recyclerView: RecyclerView,
                                       viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                }

            }
        ItemTouchHelper(simpleItemTouchCallback)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int) {

    }

}