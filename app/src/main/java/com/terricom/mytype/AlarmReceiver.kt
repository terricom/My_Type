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

                        userDocument.collection(FirebaseKey.COLLECTION_GOAL)
                            .orderBy(FirebaseKey.TIMESTAMP, Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener {

                                val itemGoals = mutableListOf<Goal>()
                                for (document in it){

                                    itemGoals.add(document.toObject(Goal::class.java))
                                }

                                ifReachGoal(items, itemGoals)
                            }
                    }
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    fun ifReachGoal(items: MutableList<Foodie>, itemsGoal: MutableList<Goal>) {

        var totalWater = 0f
        var totalOil = 0f
        var totalVegetable = 0f
        var totalProtein = 0f
        var totalFruit = 0f
        var totalCarbon = 0f

        var goalWater = "0.0"
        var goalOil = "0.0"
        var goalVegetable = "0.0"
        var goalFruit = "0.0"
        var goalProtein = "0.0"
        var goalCarbon = "0.0"


        for (today in items){
            totalWater = totalWater.plus(today.water ?: 0f)
            totalOil = totalOil.plus(today.oil ?: 0f)
            totalVegetable = totalVegetable.plus(today.vegetable ?: 0f)
            totalProtein = totalProtein.plus(today.protein ?: 0f)
            totalFruit = totalFruit.plus(today.fruit ?: 0f)
            totalCarbon = totalCarbon.plus(today.carbon ?: 0f)
        }

        itemsGoal[0].timestamp?.let {

            itemsGoal[0].let {

                goalWater = it.water.toDemicalPoint(1)
                goalVegetable = it.vegetable.toDemicalPoint(1)
                goalFruit = it.fruit.toDemicalPoint(1)
                goalCarbon = it.carbon.toDemicalPoint(1)
                goalOil = it.oil.toDemicalPoint(1)
                goalProtein = it.protein.toDemicalPoint(1)
            }
        }


        textTitle = App.applicationContext().getString(R.string.notification_title)
        textContent =
            App.applicationContext().getString(R.string.notification_today_goal)+
                    if (goalWater.toFloatFormat() > totalWater) {
                        App.applicationContext().getString(
                            R.string.notification_goal_water,
                            "${abs(goalWater.toFloatFormat().minus(totalWater))}")
                    }else {App.applicationContext().getString(R.string.notification_goal_water_reach,
                        "${abs(goalWater.toFloatFormat().minus(totalWater))}")
                    }+"   "+
                    if (goalOil.toFloatFormat() > totalOil){
                        App.applicationContext().getString(R.string.notification_goal_oil,
                            "${abs(goalOil.toFloatFormat().minus(totalOil))}")
                    }else {
                        App.applicationContext().getString(R.string.notification_goal_oil_reach,
                            "${abs(goalOil.toFloatFormat().minus(totalOil))}")
                    }+
                    if (goalVegetable.toFloatFormat() > totalVegetable){
                        App.applicationContext().getString(R.string.notification_goal_vegetable,
                            "${abs(goalVegetable.toFloatFormat().minus(totalVegetable))}")
                    }else {
                        App.applicationContext().getString(R.string.notification_goal_vegetable_reach,
                            "${abs(goalVegetable.toFloatFormat().minus(totalVegetable))}")
                    }+"       "+
                    if (goalProtein.toFloatFormat() > totalProtein){
                        App.applicationContext().getString(R.string.notification_goal_protein,
                            "${abs(goalProtein.toFloatFormat().minus(totalProtein))}")
                    }else {
                        App.applicationContext().getString(R.string.notification_goal_protein_reach,
                            "${abs(goalProtein.toFloatFormat().minus(totalProtein))}")
                    }+
                    if (goalFruit.toFloatFormat() > totalFruit){
                        App.applicationContext().getString(R.string.notification_goal_fruit,
                            "${abs(goalFruit.toFloatFormat().minus(totalFruit))}")
                    }else {
                        App.applicationContext().getString(R.string.notification_goal_fruit_reach,
                            "${abs(goalFruit.toFloatFormat().minus(totalFruit))}")
                    }+"       "+
                    if (goalCarbon.toFloatFormat() > totalCarbon){
                        App.applicationContext().getString(R.string.notification_goal_carbon,
                            "${abs(goalCarbon.toFloatFormat().minus(totalCarbon))}")
                    }else {
                        App.applicationContext().getString(R.string.notification_goal_carbon_reach,
                            "${abs(goalCarbon.toFloatFormat().minus(totalCarbon))}")
                    }

        if (textTitle.equals(App.applicationContext().getString(R.string.notification_title))){

            createNotificationChannel()
        }
    }

    companion object {
        const val receiveTitle = "title"
        const val receiveTag = "activity_app"

        private var textTitle: String ?= ""
        private var textContent: String ?= ""
        const val CHANNEL_ID = "MyType"
    }

}