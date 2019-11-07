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
import androidx.lifecycle.MutableLiveData
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.tools.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*
import kotlin.math.abs


class AlarmReceiver : BroadcastReceiver() {

    private var goalWater = MutableLiveData<String>()
    private var goalOil = MutableLiveData<String>()
    private var goalVegetable = MutableLiveData<String>()
    private var goalFruit = MutableLiveData<String>()
    private var goalProtein = MutableLiveData<String>()
    private var goalCarbon = MutableLiveData<String>()
    private var totalWater = 0.0f
    private var totalOil = 0.0f
    private var totalVegetable = 0.0f
    private var totalProtein = 0.0f
    private var totalFruit = 0.0f
    private var totalCarbon = 0.0f
    private var myTypeRepository: MyTypeRepository? = null

    override fun onReceive(context: Context, intent: Intent) {

        myTypeRepository = (context.applicationContext as App).myTypeRepository

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
        val notificationId = 0


        val builder = NotificationCompat.Builder(App.applicationContext(), CHANNEL_ID)
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
                App.applicationContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(App.applicationContext())) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    @SuppressLint("StringFormatMatches")
    private fun setMessage(){

        coroutineScope.launch {

            val foodieList = myTypeRepository!!.getObjects(
                FirebaseKey.COLLECTION_FOODIE,
                Timestamp.valueOf(
                    App.applicationContext().getString(
                        R.string.timestamp_daybegin,
                        Date().toDateFormat(FORMAT_YYYY_MM_DD))
                ),
                Timestamp.valueOf(
                    App.applicationContext().getString(
                        R.string.timestamp_dayend,
                        Date().toDateFormat(FORMAT_YYYY_MM_DD))
                )
            )

            for (foodie in foodieList as List<Foodie>){
                totalWater = totalWater.plus(foodie.water ?: 0f)
                totalOil = totalOil.plus(foodie.oil ?: 0f)
                totalVegetable = totalVegetable.plus(foodie.vegetable ?: 0f)
                totalProtein = totalProtein.plus(foodie.protein ?: 0f)
                totalFruit = totalFruit.plus(foodie.fruit ?: 0f)
                totalCarbon = totalCarbon.plus(foodie.carbon ?: 0f)
            }

            val goalList = myTypeRepository!!.getObjects(
                FirebaseKey.COLLECTION_GOAL,
                Timestamp(946656000), Timestamp(4701859200)
            )

            when (goalList.isEmpty()){
                true -> {
                    goalWater.value = 0.0f.toDemicalPoint(1)
                    goalVegetable.value = 0.0f.toDemicalPoint(1)
                    goalFruit.value= 0.0f.toDemicalPoint(1)
                    goalCarbon.value = 0.0f.toDemicalPoint(1)
                    goalOil.value = 0.0f.toDemicalPoint(1)
                    goalProtein.value = 0.0f.toDemicalPoint(1)
                }
                false -> {
                    goalWater.value = (goalList[0] as Goal).water.toDemicalPoint(1)
                    goalVegetable.value = (goalList[0] as Goal).vegetable.toDemicalPoint(1)
                    goalFruit.value= (goalList[0] as Goal).fruit.toDemicalPoint(1)
                    goalCarbon.value = (goalList[0] as Goal).carbon.toDemicalPoint(1)
                    goalOil.value = (goalList[0] as Goal).oil.toDemicalPoint(1)
                    goalProtein.value = (goalList[0] as Goal).protein.toDemicalPoint(1)
                }
            }

            textTitle = App.applicationContext().getString(R.string.notification_title)
            textContent =
                App.applicationContext().getString(R.string.notification_today_goal)+
                        if (goalWater.value.toFloatFormat() > totalWater) {
                            App.applicationContext().getString(
                                R.string.notification_goal_water,
                                "${abs(goalWater.value.toFloatFormat().minus(totalWater))}")
                        }else {App.applicationContext().getString(R.string.notification_goal_water_reach,
                            "${abs(goalWater.value.toFloatFormat().minus(totalWater))}")
                        }+"   "+
                        if (goalOil.value.toFloatFormat() > totalOil){
                            App.applicationContext().getString(R.string.notification_goal_oil,
                                "${abs(goalOil.value.toFloatFormat().minus(totalOil))}")
                        }else {
                            App.applicationContext().getString(R.string.notification_goal_oil_reach,
                                "${abs(goalOil.value.toFloatFormat().minus(totalOil))}")
                        }+
                        if (goalVegetable.value.toFloatFormat() > totalVegetable){
                            App.applicationContext().getString(R.string.notification_goal_vegetable,
                                "${abs(goalVegetable.value.toFloatFormat().minus(totalVegetable))}")
                        }else {
                            App.applicationContext().getString(R.string.notification_goal_vegetable_reach,
                                "${abs(goalVegetable.value.toFloatFormat().minus(totalVegetable))}")
                        }+"       "+
                        if (goalProtein.value.toFloatFormat() > totalProtein){
                            App.applicationContext().getString(R.string.notification_goal_protein,
                                "${abs(goalProtein.value.toFloatFormat().minus(totalProtein))}")
                        }else {
                            App.applicationContext().getString(R.string.notification_goal_protein_reach,
                                "${abs(goalProtein.value.toFloatFormat().minus(totalProtein))}")
                        }+
                        if (goalFruit.value.toFloatFormat() > totalFruit){
                            App.applicationContext().getString(R.string.notification_goal_fruit,
                                "${abs(goalFruit.value.toFloatFormat().minus(totalFruit))}")
                        }else {
                            App.applicationContext().getString(R.string.notification_goal_fruit_reach,
                                "${abs(goalFruit.value.toFloatFormat().minus(totalFruit))}")
                        }+"       "+
                        if (goalCarbon.value.toFloatFormat() > totalCarbon){
                            App.applicationContext().getString(R.string.notification_goal_carbon,
                                "${abs(goalCarbon.value.toFloatFormat().minus(totalCarbon))}")
                        }else {
                            App.applicationContext().getString(R.string.notification_goal_carbon_reach,
                                "${abs(goalCarbon.value.toFloatFormat().minus(totalCarbon))}")
                        }

            if (textTitle.equals(App.applicationContext().getString(R.string.notification_title))){

                createNotificationChannel()
            }
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