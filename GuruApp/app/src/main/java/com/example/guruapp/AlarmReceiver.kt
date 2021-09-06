package com.example.guruapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*


class AlarmReceiver  : BroadcastReceiver() {
    companion object{
        const val TAG="AlarmReceiver"
        const val NOTIFICATION_ID=0
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }
    var str = ""
    var day = 0
    //보여줄 문구
    var strArr = arrayListOf<String>("벤자민 플랭클린이 ‘기운과 끈기는 모든 것을 이겨낸다.’라는 말을 했어. 기운내고! 끈기있게! 오늘도 파이팅!\n",
        "로버트 브라우닝이 ‘단 1분의 성공이 몇 년의 실패를 보상한다’고 했어. 많이 힘들더라도 조금만 더 힘내보자!\n",
        "오프라 원프리의‘당신은 움츠리기보다 활짝 피어나도록 만들어진 존재 입니다’말처럼 너도 활~짝 필거야! 응원해!\n",
        "행복은 우리에게 건강의 근본이 되는 에너지를 준대. 공부 시작 전에 한 번 크게 하하하! 웃는건 어때?")

    lateinit var notificationManager: NotificationManager
    lateinit var str_id : String
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d(TAG, "Received intent : $intent")
        notificationManager = context?.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        var calendar : Calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DATE).toInt()

        str_id = intent?.getStringExtra("intent_id").toString()

        createNotificationChannel()
        deliverNotification(context)
    }

    private fun deliverNotification(context: Context){
        val contentIntent = Intent(context, alarm::class.java)
        contentIntent.putExtra("intent_id",str_id )
        contentIntent.putExtra("alarmTrue", true)
        contentIntent.putExtra("stopTrue", false)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        //푸시 알림의 텍스트 스타일 지정
        val style = NotificationCompat.BigTextStyle()
        style.bigText(strArr[day%4])

        //푸시 알림 생성
        val builder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_dada)
            .setContentTitle("모도리")
            .setContentText(strArr[day%4])
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setStyle(style)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
    //푸시 알림을 할때 채널이 필요해서 채널을 만드는 함수를 정의
    fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID, "Stand up notification", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "AlarmManager Tests"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}