package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.weatherapp.databinding.ActivityMainBinding
import androidx.core.view.get

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
                R.id.search-> {setFragment(searchFragment)
                    binding.bottomNav.menu.get(1).setIcon(R.drawable.ic_star_0)}
                R.id.my_cities -> {setFragment(myCitiesFragment)
                    it.setIcon(R.drawable.ic_star_1)
                }
                R.id.settings -> binding.bottomNav.menu.get(1).setIcon(R.drawable.ic_star_0)
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