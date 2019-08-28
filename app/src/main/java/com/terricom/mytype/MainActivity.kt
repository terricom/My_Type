package com.terricom.mytype

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.terricom.mytype.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    val viewModel : MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private lateinit var binding: ActivityMainBinding
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_food_record -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToDiaryFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_diary -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLinechartFragment())

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_line_chart -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToHarvestFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_harvest -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    // get the height of status bar from system
    private val statusBarHeight: Int
        get() {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return when {
                resourceId > 0 -> resources.getDimensionPixelSize(resourceId)
                else -> 0
            }
        }

    private val duration = 1000L
    private val await = 1500L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        setupNavController()

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }


    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.foodieFragment -> CurrentFragmentType.FOODIE
                R.id.diaryFragment -> CurrentFragmentType.DIARY
                R.id.linechartFragment -> CurrentFragmentType.LINECHART
                R.id.harvestFragment -> CurrentFragmentType.HARVEST
                R.id.loginFragment -> CurrentFragmentType.LOGIN
                R.id.shaperecordFragment -> CurrentFragmentType.SHAPE_RECORD
                R.id.referenceDialog -> CurrentFragmentType.REF
                else -> viewModel.currentFragmentType.value
            }
        }
        viewModel.currentFragmentType.observe(this, Observer {
            Log.i("Terri", "viewModel.currentFragmentType.observe = ${it.value}")
            binding.textToolbarTitle.text = it.value
            if (it.value == ""){
                hideBottomNavView()
                hideToolbar()
            }
            if (it.value == App.instance?.getString(R.string.title_foodie) || it.value == App.instance?.getString(R.string.title_shape_record) ){
                hideBottomNavView()
            }
        })
    }

    fun hideToolbar(){
        binding.toolbar.visibility = View.GONE
    }

    fun hideBottomNavView(){
        binding.bottomNavView.visibility = View.GONE
    }



}
