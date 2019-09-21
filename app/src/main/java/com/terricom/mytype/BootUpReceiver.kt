package com.terricom.mytype

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.UserManager
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

private var alarmMgr: AlarmManager? = null
private lateinit var alarmIntent: PendingIntent

class BootUpReceiver : BroadcastReceiver() {

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


    override fun onReceive(context: Context, intent: Intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //        throw new UnsupportedOperationException("Not yet implemented");

        /* 同一個接收者可以收多個不同行為的廣播，所以可以判斷收進來的行為為何，再做不同的動作 */
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            /* 收到廣播後要做的事 */
//            setMessage()
//
//            Handler().postDelayed({
//                createNotificationChannel()
//            },5000)

            alarmMgr = App.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmIntent = Intent(App.applicationContext(), AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(App.applicationContext(), 0, intent, 0)
            }

            // Set the alarm to start at 12:30 a.m.
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

            //建立通知發布鬧鐘
//            val calendar: Calendar = Calendar.getInstance().apply {
//                timeInMillis = System.currentTimeMillis()
//                set(Calendar.HOUR_OF_DAY, 20)
//                add(Calendar.MINUTE, 35)
//
//            }
//            add_alarm(context, calendar)

//            val cal = GregorianCalendar(TimeZone.getTimeZone("GMT+8:00")) //取得時間
//
//            cal.add(Calendar.MINUTE, 1)    //加一分鐘
//            cal.set(Calendar.SECOND, 0)    //設定秒數為0
//            add_alarm(context, cal)        //註冊鬧鐘

        }


//        //取消開機執行
//        val receiver = ComponentName(this, BootUpReceiver::class.java)
//        val pm = this.getPackageManager()
//        pm.setComponentEnabledSetting(
//            receiver,
//            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//            PackageManager.DONT_KILL_APP
//        )
    }

    /***    加入(與系統註冊)鬧鐘     */
    fun add_alarm(context: Context, cal: Calendar) {
        Logger.d(
            "alarm add time: " + "${cal.get(Calendar.MONTH)}" + "." + "${cal.get(Calendar.DATE)}"
                    + " " + "${cal.get(Calendar.HOUR_OF_DAY)}" + ":" + "${cal.get(Calendar.MINUTE)}" + ":" + "${cal.get(
                Calendar.SECOND
            )}")

        val intent = Intent(context, AlarmReceiver::class.java)
        // 以日期字串組出不同的 category 以添加多個鬧鐘
        intent.addCategory(
            "ID." + "${cal.get(Calendar.MONTH)}" + "." + "${cal.get(Calendar.DATE)}" + "-" +
                    "${cal.get(Calendar.HOUR_OF_DAY)}"
                    + "." + "${cal.get(Calendar.MINUTE)}" + "." +
                    "${cal.get(
                        Calendar.SECOND
                    )}"
        )

        val AlarmTimeTag =
            "Alarmtime " + "${cal.get(Calendar.HOUR_OF_DAY)}" + ":" +"${cal.get(Calendar.MINUTE)}" + ":" + "${cal.get(Calendar.SECOND)}"

        intent.putExtra("title", "activity_app")
        intent.putExtra("time", AlarmTimeTag)

        val pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)       //註冊鬧鐘
    }

    private fun createNotificationChannel() {


        val intent: Intent? = Intent(App.applicationContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(App.applicationContext(), 0, intent, 0)
        var notificationId = 0


        var builder = NotificationCompat.Builder(App.applicationContext(), CHANNEL_ID)
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
                App.applicationContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(App.applicationContext())) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    private fun setMessage(){
        val db = FirebaseFirestore.getInstance()
        val users = db.collection("Users")
        val userUid = UserManager.uid
        val sdf = SimpleDateFormat("yyyy-MM-dd")


        if (userUid != null){
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

                            textTitle = "好的開始是成功的一半"
                            textContent =
                                "距離今日目標\n\uD83D\uDCA7飲水量還差 ${if (diffWater!! <= 0.0f) 0.0f else diffWater} 份  " +
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