package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.weatherapp.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView;
import com.example.weatherapp.database.AppDatabase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val searchFragment = SearchFragment()
        val myCitiesFragment = MyCitiesFragment()
//        val settingsFragment = SettingsFragment()

        //sets default fragment
        setFragment(searchFragment)

        binding.bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.search-> setFragment(searchFragment)
                R.id.my_cities -> setFragment(myCitiesFragment)
//                R.id.settings -> setFragment(settingsFragment)
            }
            true
        }



    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_container, fragment)
            commit()
        }
    }

    private fun isNetworkConnected(): Boolean {
        //1
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //2
        val activeNetwork = connectivityManager.activeNetwork
        //3
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        //4
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }



}