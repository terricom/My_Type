package com.terricom.mytype

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.Shape
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity(){

    val viewModel : MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private lateinit var binding: ActivityMainBinding
    var isFABOpen: Boolean = false
    val time = Calendar.getInstance().time
    private var textTitle: String ?= ""
    private var textContent: String ?= ""
    val CHANNEL_ID = "MyType"

    val _fireFoodie = MutableLiveData<List<Foodie>>()
    val fireFoodie: LiveData<List<Foodie>>
        get() = _fireFoodie

    fun fireFoodieBack (foo: List<Foodie>){
        _fireFoodie.value = foo
    }

    val totalWater = MutableLiveData<Float>()

    val totalOil = MutableLiveData<Float>()

    val totalVegetable = MutableLiveData<Float>()

    val totalProtein = MutableLiveData<Float>()

    val totalFruit = MutableLiveData<Float>()

    val totalCarbon = MutableLiveData<Float>()

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_diary -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToDiaryFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_accumulation -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLinechartFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_achievment -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToAchivementFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton

        fab.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }
        binding.fab1.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToFoodieFragment(
                Foodie()
            ))
            binding.fabShadow.visibility = View.GONE
        }

        binding.fab2.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToShapeRecordFragment(
                Shape()
            ))
            binding.fabShadow.visibility = View.GONE

        }
        binding.fab3.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToSleepFragment())
            binding.fabShadow.visibility = View.GONE

        }
        binding.fabLayout3.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToSleepFragment())
            binding.fabShadow.visibility = View.GONE

        }
        binding.fab4.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToGoalSettingFragment(
                Goal()
            ))
            binding.fabShadow.visibility = View.GONE

        }
        binding.fabLayout4.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToGoalSettingFragment(
                Goal()
            ))
            binding.fabShadow.visibility = View.GONE

        }

        binding.fabLayout1.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToFoodieFragment(Foodie()))
            binding.fabShadow.visibility = View.GONE

        }
        binding.fabLayout2.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToShapeRecordFragment(
                Shape()
            ))
            binding.fabShadow.visibility = View.GONE

        }


        setupNavController()

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Logger.w( "Firebase getInstanceId failed ${task.exception}")
//                    return@OnCompleteListener
//                }
//
//                // Get new Instance ID token
//                val token = task.result?.token
//
//                // Log and toast
//                Logger.d("Firebase token from firebase =$token")
//            })

        alarmMgr = App.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(App.applicationContext(), AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(App.applicationContext(), 0, intent, 0)
        }

        // Set the alarm to start at 8:30 a.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 30)
        }

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.
        alarmMgr?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 20,
            alarmIntent
        )

//        //開機執行
//        val receiver = ComponentName(App.applicationContext(), BootUpReceiver::class.java)
//        val pm = App.applicationContext().packageManager
//        pm.setComponentEnabledSetting(
//            receiver,
//            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//            PackageManager.DONT_KILL_APP
//        )

//        setMessage()
//        Handler().postDelayed({
//            createNotificationChannel()
//        }, 5000)

    }


    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.foodieFragment -> CurrentFragmentType.FOODIE
                R.id.diaryFragment -> CurrentFragmentType.DIARY
                R.id.linechartFragment -> CurrentFragmentType.LINECHART
                R.id.achivementFragment -> CurrentFragmentType.HARVEST
                R.id.loginFragment -> CurrentFragmentType.LOGIN
                R.id.shaperecordFragment -> CurrentFragmentType.SHAPE_RECORD
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.dreamboardFragment -> CurrentFragmentType.DREAMBOARD
                R.id.sleepFragment -> CurrentFragmentType.SLEEP
                R.id.goalSettingFragment -> CurrentFragmentType.GOAL
                else -> viewModel.currentFragmentType.value
            }
        }
        viewModel.currentFragmentType.observe(this, Observer {
            Log.i("Terri", "viewModel.currentFragmentType.observe = ${it.value}")
//            binding.textToolbarTitle.text = it.value
            if (it.value == ""){
                hideBottomNavView()
                hideToolbar()
                hideFABView()
//                Handler().postDelayed({
//                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToDiaryFragment())
//                    binding.fabShadow.visibility = View.GONE
//                    fabLayout1.animate().translationY(resources.getDimension(R.dimen.standard_0))
//                    fabLayout2.animate().translationY(resources.getDimension(R.dimen.standard_0))
//                    fabLayout3.animate().translationY(resources.getDimension(R.dimen.standard_0))
//                    fabLayout4.animate().translationY(resources.getDimension(R.dimen.standard_0))
//                    binding.fab.visibility = View.VISIBLE
//                    binding.bottomNavView.visibility = View.VISIBLE
//                },2000)
            }
            if (it.value == App.instance?.getString(R.string.title_foodie) ||
                it.value == App.instance?.getString(R.string.title_shape_record) ||
                it.value == App.instance?.getString(R.string.title_sleep) ||
                it.value == App.instance?.getString(R.string.title_goal_setting) ||
                it.value == App.instance?.getString(R.string.title_dream_puzzle) ){
                hideBottomNavView()
                hideFABView()
            }

        })
    }

    fun hideToolbar(){
//        binding.toolbar.visibility = View.GONE
    }

    fun hideBottomNavView(){
        binding.bottomNavView.visibility = View.GONE
    }

    fun hideFABView(){
        binding.fab.visibility = View.GONE
        binding.fabLayout1.visibility = View.GONE
        binding.fabLayout2.visibility = View.GONE
        binding.fabLayout3.visibility - View.GONE
        binding.fabLayout4.visibility - View.GONE

        binding.fab1.visibility = View.GONE
        binding.fab2.visibility = View.GONE
        binding.fab3.visibility = View.GONE
        binding.fab4.visibility = View.GONE

    }


    fun showFABMenu() {
        isFABOpen = true
        fabLayout1.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        fabLayout2.animate().translationY(-resources.getDimension(R.dimen.standard_105))
        fabLayout3.animate().translationY(-resources.getDimension(R.dimen.standard_155))
        fabLayout4.animate().translationY(-resources.getDimension(R.dimen.standard_205))
        fab.animate().rotation(45.0f)
        fab_custom_pic.animate().rotation(45.0f)
        binding.fabShadow.visibility = View.VISIBLE
        binding.fab.visibility = View.VISIBLE
        binding.fab1.visibility = View.VISIBLE
        binding.fab2.visibility = View.VISIBLE
        binding.fab3.visibility = View.VISIBLE
        binding.fab4.visibility = View.VISIBLE
        binding.fabLayout1.visibility = View.VISIBLE
        binding.fabLayout2.visibility = View.VISIBLE
        binding.fabLayout3.visibility = View.VISIBLE
        binding.fabLayout4.visibility = View.VISIBLE
    }

    fun closeFABMenu() {
        isFABOpen = false
        binding.fabShadow.visibility = View.GONE
        fab.animate().rotation(90.0f)
        fab_custom_pic.animate().rotation(90.0f)
        fabLayout1.animate().translationY(resources.getDimension(R.dimen.standard_0))
        fabLayout2.animate().translationY(resources.getDimension(R.dimen.standard_0))
        fabLayout3.animate().translationY(resources.getDimension(R.dimen.standard_0))
        fabLayout4.animate().translationY(resources.getDimension(R.dimen.standard_0))


        Handler().postDelayed({
            binding.fabLayout1.visibility = View.INVISIBLE
            binding.fabLayout2.visibility = View.INVISIBLE
            binding.fabLayout3.visibility = View.INVISIBLE
            binding.fabLayout4.visibility = View.INVISIBLE}, 300)
    }

    private fun createNotificationChannel() {


        val intent: Intent? = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        var notificationId = 0


        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_my_type)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(textContent))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(App.applicationContext().resources.getColor(R.color.colorMyType))


        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "notify"
            val descriptionText = "countdown"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    private fun setMessage(){
        val db = FirebaseFirestore.getInstance()
        val users = db.collection("Users")
        val userUid = UserManager.uid
        val sdf = SimpleDateFormat("yyyy-MM-dd")


        if (userUid!!.isNotEmpty()){
            val foodieDiary = users
                .document(userUid).collection("Foodie")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("timestamp", Timestamp.valueOf("${sdf.format(Date())} 00:00:00.000000000"))

            val goal = users
                .document(userUid).collection("Goal")
                .orderBy("timestamp", Query.Direction.DESCENDING)

            val goalWater = MutableLiveData<Float>()
            val goalOil = MutableLiveData<Float>()
            val goalVegetable = MutableLiveData<Float>()
            val goalFruit = MutableLiveData<Float>()
            val goalProtein = MutableLiveData<Float>()
            val goalCarbon = MutableLiveData<Float>()

            goal
                .get()
                .addOnSuccessListener {
                    if (it.size() != 0){
                        val latestGoal = it.documents[0].toObject(Goal::class.java)
                        latestGoal?.let {
                            goalWater.value = it.water
                            goalVegetable.value = it.vegetable
                            goalFruit.value = it.fruit
                            goalCarbon.value = it.carbon
                            goalOil.value = it.oil
                            goalProtein.value = it.protein
                        }
                    }else{
                        goalWater.value = 0.0f
                        goalOil.value = 0.0f
                        goalVegetable.value = 0.0f
                        goalProtein.value = 0.0f
                        goalFruit.value = 0.0f
                        goalCarbon.value = 0.0f
                    }
                }


            foodieDiary
                .get()
                .addOnSuccessListener {
                    if (it.size() == 0){
                        textTitle = "Hi 今天吃得好嗎？"
                        textContent = "快來新增食記 記下營養滿滿的一天吧！"
                    }else {
                        val items = mutableListOf<Foodie>()
                        for (fooDiary in it){
                            items.add(fooDiary.toObject(Foodie::class.java))
                        }
                        fireFoodieBack(items)
                        Logger.i("FireFoodie.value = ${fireFoodie.value}")
                        totalWater.value = 0f
                        totalOil.value = 0f
                        totalVegetable.value = 0f
                        totalProtein.value = 0f
                        totalFruit.value = 0f
                        totalCarbon.value = 0f
                        for (today in fireFoodie.value!!){
                            totalWater.value = totalWater.value!!.plus(today.water ?: 0f)
                            totalOil.value = totalOil.value!!.plus(today.oil ?: 0f)
                            totalVegetable.value = totalVegetable.value!!.plus(today.vegetable ?: 0f)
                            totalProtein.value = totalProtein.value!!.plus(today.protein ?: 0f)
                            totalFruit.value = totalFruit.value!!.plus(today.fruit ?: 0f)
                            totalCarbon.value = totalCarbon.value!!.plus(today.carbon ?: 0f)
                        }

                        Handler().postDelayed({
                            Logger.i("goalWater =${goalWater.value} totalWater = ${totalWater.value}")

                            val diffWater = goalWater.value?.minus(totalWater.value!!.toFloat())
                            val diffOil = goalOil.value?.minus(totalOil.value!!.toFloat())
                            val diffVegetable =
                                goalVegetable.value?.minus(totalVegetable.value!!.toFloat())
                            val diffProtein =
                                goalProtein.value?.minus(totalProtein.value!!.toFloat())
                            val diffFruit = goalFruit.value?.minus(totalFruit.value!!.toFloat())
                            val diffCarbon = goalCarbon.value?.minus(totalCarbon.value!!.toFloat())

                            textTitle = "好的開始是成功的一半 距離今日目標"
                            textContent =
                                "\uD83D\uDCA7飲水量還差 ${if (diffWater!! <= 0.0f) 0.0f else diffWater} 份  " +
                                        "\uD83E\uDD51油脂還差 ${if (diffOil!! <= 0.0f) 0.0f else diffOil} 份\n " +
                                        "\uD83E\uDD66蔬菜還差 ${if (diffVegetable!! <= 0.0f) 0.0f else diffVegetable} 份  " +
                                        "\uD83C\uDF73蛋白質還差 ${if (diffProtein!! <= 0.0f) 0.0f else diffProtein} 份\n" +
                                        "\uD83C\uDF4E水果還差 ${if (diffFruit!! <= 0.0f) 0.0f else diffFruit} 份  " +
                                        "\uD83E\uDD54碳水還差 ${if (diffCarbon!! <= 0.0f) 0.0f else diffCarbon} 份"

                        },2000)

                    }
                }


        }


    }


}
