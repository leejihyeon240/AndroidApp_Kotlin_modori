package com.example.guruapp

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import org.w3c.dom.Text
import java.time.LocalDate


class MyAdapterAlarm( private val items : MutableList<MyAlarmItem>) : BaseAdapter() {
    private val strArr = arrayListOf<Int>(R.drawable.notification_bubble1, R.drawable.notification_bubble2, R.drawable.notification_bubble3, R.drawable.notification_bubble4)

    @RequiresApi(Build.VERSION_CODES.O)  //LocalDate.now()를 사용하기 위함
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var convertView = p1
        if(convertView == null){
            convertView = LayoutInflater.from(p2?.context).inflate(R.layout.alarm_layout, p2, false)
        }
        val item : MyAlarmItem = items[p0]

        var imgAlarm = convertView?.findViewById<ImageView>(R.id.alarmIcon)
        var tvDate = convertView?.findViewById<TextView>(R.id.alarmDate)
        var imgDelete = convertView?.findViewById<ImageView>(R.id.alarmDelete)
        var imgContent = convertView?.findViewById<ImageView>(R.id.alarmImage)

        //레벨업 알람 외의 일반 알람일때 캐릭터의 얼굴 설정
        if(!item.plainAlarm){
            imgAlarm?.setImageResource(R.drawable.notification_dada)
            //매일 반복해서 알람이 생성될 수 있도록 4의 나머지 값으로 알람 설정
            imgContent?.setImageResource(strArr[item.date%4])
        }else{
            imgAlarm?.setImageResource(R.drawable.notification_dada2)
            //레벨업 했을 때 이미지로 설정
            imgContent?.setImageResource(R.drawable.notification_bubblelevelup)
        }

        //날짜 텍스트값 설정
        tvDate?.setText("" + item.nowDate)

        //삭제 버튼이 눌렸을 때 아이템 삭제
        imgDelete?.setOnClickListener{
            items.removeAt(p0)
            notifyDataSetChanged()
        }

        return convertView!!
    }

    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}