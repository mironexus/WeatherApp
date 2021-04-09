package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.weatherapp.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView;

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchFragment = SearchFragment()
//        val myCitiesFragment = MyCitiesFragment()
//        val settingsFragment = SettingsFragment()

        //sets default fragment
        setFragment(searchFragment)

        binding.bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.search-> setFragment(searchFragment)
//                R.id.myCities -> setFragment(myCitiesFragment)
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



}