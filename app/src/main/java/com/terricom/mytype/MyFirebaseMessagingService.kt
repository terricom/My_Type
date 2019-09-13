package com.terricom.mytype

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.UserManager
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class MyFirebaseMessagingService: FirebaseMessagingService(){

    override fun onCreate() {
        super.onCreate()
        setMessage()
        Handler().postDelayed({
            createNotificationChannel()
        },4000)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Logger.d("From: " + remoteMessage.from!!)

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Logger.d("Message data payload: " + remoteMessage.data)
            setMessage()
            Handler().postDelayed({
                createNotificationChannel()
            },4000)

        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Logger.d( "Message Notification Body: " + remoteMessage.notification!!.body!!)
            setMessage()
//            Handler().postDelayed({
                createNotificationChannel()
//            },4000)
        }

    }

    var time = 0
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
                                "距離今日目標的\uD83D\uDCA7飲水量還差 ${if (diffWater!! <= 0.0f) 0.0f else diffWater} 份  " +
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

    private var textTitle: String ?= ""
    private var textContent: String ?= ""


    private fun createNotificationChannel() {


        val intent: Intent? = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        var notificationId = time


        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_my_type)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setStyle(NotificationCompat.BigTextStyle()
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
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }
}