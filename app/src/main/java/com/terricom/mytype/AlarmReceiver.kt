package com.terricom.mytype

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.tools.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class AlarmReceiver : BroadcastReceiver() {



    private val _fireFoodie = MutableLiveData<List<Foodie>>()
    private val fireFoodie: LiveData<List<Foodie>>
        get() = _fireFoodie

    private fun fireFoodieBack (foo: List<Foodie>){
        _fireFoodie.value = foo
    }

    override fun onReceive(context: Context, intent: Intent) {

        Logger.i("AlarmReceiver onReceive time = ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())}")

        intent.extras?.let {

            if (it.get(receiveTitle) == receiveTag && Date().toDateFormat(FORMAT_HH_MM).split(":")[0] == "12"){

                setMessage()
            }
        }
    }

    private fun createNotificationChannel() {


        val intent: Intent? = Intent(App.applicationContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(App.applicationContext(), 0, intent, 0)
        var notificationId = 0


        var builder = NotificationCompat.Builder(App.applicationContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_my_type)
            .setContentTitle(Companion.textTitle)
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
                App.applicationContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(App.applicationContext())) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun setMessage(){

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {userDocument ->

                userDocument.collection(FirebaseKey.COLLECTION_FOODIE)
                    .orderBy(FirebaseKey.TIMESTAMP, Query.Direction.DESCENDING)
                    .whereGreaterThanOrEqualTo(FirebaseKey.TIMESTAMP, Timestamp.valueOf(Date().toDateFormat(
                        FORMAT_YYYY_MM_DD_HH_MM_SS_FFFFFFFFF
                    )))
                    .get()
                    .addOnSuccessListener {

                        val items = mutableListOf<Foodie>()
                        for (fooDiary in it){
                            items.add(fooDiary.toObject(Foodie::class.java))
                        }
                        fireFoodieBack(items)
                        Logger.i("FireFoodie.value = ${fireFoodie.value}")
                        Companion.totalWater.value = 0f
                        totalOil.value = 0f
                        totalVegetable.value = 0f
                        totalProtein.value = 0f
                        totalFruit.value = 0f
                        totalCarbon.value = 0f
                        for (today in fireFoodie.value!!){
                            Companion.totalWater.value = Companion.totalWater.value!!.plus(today.water ?: 0f)
                            totalOil.value = totalOil.value!!.plus(today.oil ?: 0f)
                            totalVegetable.value = totalVegetable.value!!.plus(today.vegetable ?: 0f)
                            totalProtein.value = totalProtein.value!!.plus(today.protein ?: 0f)
                            totalFruit.value = totalFruit.value!!.plus(today.fruit ?: 0f)
                            totalCarbon.value = totalCarbon.value!!.plus(today.carbon ?: 0f)
                        }

                        userDocument.collection(FirebaseKey.COLLECTION_GOAL)
                            .orderBy(FirebaseKey.TIMESTAMP, Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener {

                                val items = mutableListOf<Goal>()
                                for (document in it){

                                    items.add(document.toObject(Goal::class.java))
                                }
                                if (items.size > 0){

                                    items[0].let {
                                        goalWater.value = it.water.toDemicalPoint(1)
                                        goalVegetable.value = it.vegetable.toDemicalPoint(1)
                                        goalFruit.value = it.fruit.toDemicalPoint(1)
                                        goalCarbon.value = it.carbon.toDemicalPoint(1)
                                        goalOil.value = it.oil.toDemicalPoint(1)
                                        goalProtein.value = it.protein.toDemicalPoint(1)
                                    }
                                }else{
                                    goalWater.value = 0.0f.toDemicalPoint(1)
                                    goalOil.value = 0.0f.toDemicalPoint(1)
                                    goalVegetable.value = 0.0f.toDemicalPoint(1)
                                    goalProtein.value = 0.0f.toDemicalPoint(1)
                                    goalFruit.value = 0.0f.toDemicalPoint(1)
                                    goalCarbon.value = 0.0f.toDemicalPoint(1)
                                }
                            }


                        textTitle = App.applicationContext().getString(R.string.notification_title)
                        textContent =
                            App.applicationContext().getString(R.string.notification_today_goal)+
                                    if (goalWater.value.toFloatFormat() > Companion.totalWater.value!!) {
                                        App.applicationContext().getString(
                                            R.string.notification_goal_water,
                                            "${abs(goalWater.value.toFloatFormat().minus(Companion.totalWater.value!!))}")
                                    }else {App.applicationContext().getString(R.string.notification_goal_water_reach,
                                        "${abs(goalWater.value.toFloatFormat().minus(Companion.totalWater.value!!))}")
                                    }+"   "+
                                    if (goalOil.value.toFloatFormat() > totalOil.value!!){
                                        App.applicationContext().getString(R.string.notification_goal_oil,
                                            "${abs(goalOil.value.toFloatFormat().minus(totalOil.value!!))}")
                                    }else {
                                        App.applicationContext().getString(R.string.notification_goal_oil_reach,
                                            "${abs(goalOil.value.toFloatFormat().minus(totalOil.value!!))}")
                                    }+
                                    if (goalVegetable.value.toFloatFormat() > totalVegetable.value!!){
                                        App.applicationContext().getString(R.string.notification_goal_vegetable,
                                            "${abs(goalVegetable.value.toFloatFormat().minus(totalVegetable.value!!))}")
                                    }else {
                                        App.applicationContext().getString(R.string.notification_goal_vegetable_reach,
                                            "${abs(goalVegetable.value.toFloatFormat().minus(totalVegetable.value!!))}")
                                    }+"       "+
                                    if (goalProtein.value.toFloatFormat() > totalProtein.value!!){
                                        App.applicationContext().getString(R.string.notification_goal_protein,
                                            "${abs(goalProtein.value.toFloatFormat().minus(totalProtein.value!!))}")
                                    }else {
                                        App.applicationContext().getString(R.string.notification_goal_protein_reach,
                                            "${abs(goalProtein.value.toFloatFormat().minus(totalProtein.value!!))}")
                                    }+
                                    if (goalFruit.value.toFloatFormat() > totalFruit.value!!){
                                        App.applicationContext().getString(R.string.notification_goal_fruit,
                                            "${abs(goalFruit.value.toFloatFormat().minus(totalFruit.value!!))}")
                                    }else {
                                        App.applicationContext().getString(R.string.notification_goal_fruit_reach,
                                            "${abs(goalFruit.value.toFloatFormat().minus(totalFruit.value!!))}")
                                    }+"       "+
                                    if (goalCarbon.value.toFloatFormat() > totalCarbon.value!!){
                                        App.applicationContext().getString(R.string.notification_goal_carbon,
                                            "${abs(goalCarbon.value.toFloatFormat().minus(totalCarbon.value!!))}")
                                    }else {
                                        App.applicationContext().getString(R.string.notification_goal_carbon_reach,
                                            "${abs(goalCarbon.value.toFloatFormat().minus(totalCarbon.value!!))}")
                                    }

                        if (textTitle.equals(App.applicationContext().getString(R.string.notification_title))){

                            createNotificationChannel()
                        }
                    }
            }
        }
    }

    companion object {
        const val receiveTitle = "title"
        const val receiveTag = "activity_app"
        val goalWater = MutableLiveData<String>()
        val goalOil = MutableLiveData<String>()
        val goalVegetable = MutableLiveData<String>()
        val goalFruit = MutableLiveData<String>()
        val goalProtein = MutableLiveData<String>()
        val goalCarbon = MutableLiveData<String>()
        val totalWater = MutableLiveData<Float>()
        val totalOil = MutableLiveData<Float>()
        val totalVegetable = MutableLiveData<Float>()
        val totalProtein = MutableLiveData<Float>()
        val totalFruit = MutableLiveData<Float>()
        val totalCarbon = MutableLiveData<Float>()
        private var textTitle: String ?= ""
        private var textContent: String ?= ""
        const val CHANNEL_ID = "MyType"
    }

}