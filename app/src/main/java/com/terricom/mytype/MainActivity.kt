package com.terricom.mytype

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.terricom.mytype.data.*
import com.terricom.mytype.data.UserManager.name
import com.terricom.mytype.data.UserManager.uid
import com.terricom.mytype.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity(){

    val viewModel : MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private lateinit var binding: ActivityMainBinding
    private var isFABOpen: Boolean = false

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    private var firebaseAnalytics: FirebaseAnalytics ?= null

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_diary -> {

                if (viewModel.currentFragmentType.value != CurrentFragmentType.DIARY){

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToDiaryFragment())
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_accumulation -> {

                if (viewModel.currentFragmentType.value != CurrentFragmentType.LINE_CHART){

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLinechartFragment())
                }

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_achievment -> {

                if (viewModel.currentFragmentType.value != CurrentFragmentType.ACHIEVEMENT){

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToAchivementFragment())
                }

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {

                if (viewModel.currentFragmentType.value != CurrentFragmentType.PROFILE){
                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToProfileFragment())
                }

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserManager.getPuzzleNewUser = "0"
        UserManager.getPuzzleOldUser = "0"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "userUid = $uid");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "userName = $name");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton

        fab.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }
        binding.fabFoodie.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToFoodieFragment(
                Foodie()
            ))
            binding.fabShadow.visibility = View.GONE
            closeFABMenu()
        }

        binding.fabShape.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToShapeRecordFragment(
                Shape()
            ))
            binding.fabShadow.visibility = View.GONE
            closeFABMenu()

        }
        binding.fabSleep.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToSleepFragment(
                Sleep()
            ))
            binding.fabShadow.visibility = View.GONE
            closeFABMenu()

        }
        binding.fabLayoutSleep.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToSleepFragment(
                Sleep()
            ))
            binding.fabShadow.visibility = View.GONE
            closeFABMenu()

        }
        binding.fabGoal.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToGoalSettingFragment(
                Goal()
            ))
            binding.fabShadow.visibility = View.GONE
            closeFABMenu()

        }
        binding.fabLayoutGoal.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToGoalSettingFragment(
                Goal()
            ))
            binding.fabShadow.visibility = View.GONE
            closeFABMenu()

        }

        binding.fabLayoutFoodie.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToFoodieFragment(Foodie()))
            binding.fabShadow.visibility = View.GONE
            closeFABMenu()

        }
        binding.fabLayoutShape.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToShapeRecordFragment(
                Shape()
            ))
            binding.fabShadow.visibility = View.GONE
            closeFABMenu()

        }


        setupNavController()

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val notificationKey = "title"
        val notificationIntentValue = "activity_app"

        alarmMgr = App.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(App.applicationContext(), AlarmReceiver::class.java).let { intent ->
            intent.putExtra(notificationKey,notificationIntentValue)
            PendingIntent.getBroadcast(App.applicationContext(), 0, intent, 0)
        }

        // Set the alarm to start at 12:30 p.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 30)
        }

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 1 day
        alarmMgr?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 60 * 24,
            alarmIntent
        )

    }


    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.foodieFragment -> CurrentFragmentType.FOODIE
                R.id.diaryFragment -> CurrentFragmentType.DIARY
                R.id.lineChartFragment -> CurrentFragmentType.LINE_CHART
                R.id.achivementFragment -> CurrentFragmentType.ACHIEVEMENT
                R.id.loginFragment -> CurrentFragmentType.LOGIN
                R.id.shaperecordFragment -> CurrentFragmentType.SHAPE_RECORD
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.sleepFragment -> CurrentFragmentType.SLEEP
                R.id.goalSettingFragment -> CurrentFragmentType.GOAL
                else -> viewModel.currentFragmentType.value
            }
        }
        viewModel.currentFragmentType.observe(this, Observer {
            Log.i("Terri", "viewModel.currentFragmentType.observe = ${it.value}")
            if (it.value == ""){
                hideBottomNavView()
                hideFABView()
            }
            if (it.value == App.instance?.getString(R.string.title_foodie) ||
                it.value == App.instance?.getString(R.string.title_shape_record) ||
                it.value == App.instance?.getString(R.string.title_sleep) ||
                it.value == App.instance?.getString(R.string.title_goal_setting)){
                hideBottomNavView()
                hideFABView()
            }

        })
    }

    private fun hideBottomNavView(){
        binding.bottomNavView.visibility = View.GONE
    }

    private fun hideFABView(){
        binding.fab.visibility = View.GONE
        binding.fabLayoutFoodie.visibility = View.GONE
        binding.fabLayoutShape.visibility = View.GONE
        binding.fabLayoutSleep.visibility = View.GONE
        binding.fabLayoutGoal.visibility = View.GONE

        binding.fabFoodie.visibility = View.INVISIBLE
        binding.fabShape.visibility = View.INVISIBLE
        binding.fabSleep.visibility = View.INVISIBLE
        binding.fabGoal.visibility = View.INVISIBLE

    }


    private fun showFABMenu() {
        when (fabLayout_foodie.y){
            resources.getDimension(R.dimen.standard_0) -> fabLayout_foodie.visibility = View.INVISIBLE
            else -> fabLayout_foodie.visibility = View.VISIBLE
        }
        when (fabLayout_shape.y){
            resources.getDimension(R.dimen.standard_0) -> fabLayout_shape.visibility = View.INVISIBLE
            else -> fabLayout_shape.visibility = View.VISIBLE
        }
        when (fabLayout_sleep.y){
            resources.getDimension(R.dimen.standard_0) -> fabLayout_sleep.visibility = View.INVISIBLE
            else -> fabLayout_sleep.visibility = View.VISIBLE
        }
        when (fabLayout_goal.y){
            resources.getDimension(R.dimen.standard_0) -> fabLayout_foodie.visibility = View.INVISIBLE
            else -> fabLayout_goal.visibility = View.VISIBLE
        }
        isFABOpen = true
        fabLayout_foodie.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        fabLayout_shape.animate().translationY(-resources.getDimension(R.dimen.standard_105))
        fabLayout_sleep.animate().translationY(-resources.getDimension(R.dimen.standard_155))
        fabLayout_goal.animate().translationY(-resources.getDimension(R.dimen.standard_205))
        fab.animate().rotation(45.0f)
        fab_custom_pic.animate().rotation(45.0f)
        binding.fabShadow.visibility = View.VISIBLE
        binding.fab.visibility = View.VISIBLE
    }

    fun closeFABMenu() {

        fab_foodie.visibility = View.VISIBLE
        fab_shape.visibility = View.VISIBLE
        fab_sleep.visibility = View.VISIBLE
        fab_goal.visibility = View.VISIBLE

        when (fabLayout_foodie.y){
            resources.getDimension(R.dimen.standard_0) -> fabLayout_foodie.visibility = View.INVISIBLE
            else -> fabLayout_foodie.visibility = View.VISIBLE
        }
        when (fabLayout_shape.y){
            resources.getDimension(R.dimen.standard_0) -> fabLayout_shape.visibility = View.INVISIBLE
            else -> fabLayout_shape.visibility = View.VISIBLE
        }
        when (fabLayout_sleep.y){
            resources.getDimension(R.dimen.standard_0) -> fabLayout_sleep.visibility = View.INVISIBLE
            else -> fabLayout_sleep.visibility = View.VISIBLE
        }
        when (fabLayout_goal.y){
            resources.getDimension(R.dimen.standard_0) -> fabLayout_foodie.visibility = View.INVISIBLE
            else -> fabLayout_goal.visibility = View.VISIBLE
        }

        isFABOpen = false
        binding.fabShadow.visibility = View.GONE
        fab.animate().rotation(90.0f)
        fab_custom_pic.animate().rotation(90.0f)
        fabLayout_foodie.animate().translationY(resources.getDimension(R.dimen.standard_0))
        fabLayout_shape.animate().translationY(resources.getDimension(R.dimen.standard_0))
        fabLayout_sleep.animate().translationY(resources.getDimension(R.dimen.standard_0))
        fabLayout_goal.animate().translationY(resources.getDimension(R.dimen.standard_0))

    }

    fun backFromEditPage(){

        fabLayout_foodie.visibility = View.INVISIBLE
        fabLayout_shape.visibility = View.INVISIBLE
        fabLayout_sleep.visibility = View.INVISIBLE
        fabLayout_goal.visibility = View.INVISIBLE
        isFABOpen = false

    }

    fun back2DiaryFragment(){

        bottom_nav_view!!.visibility = View.VISIBLE
        bottom_nav_view.selectedItemId = R.id.navigation_diary
        fab.visibility = View.VISIBLE
        closeFABMenu()
    }

}
