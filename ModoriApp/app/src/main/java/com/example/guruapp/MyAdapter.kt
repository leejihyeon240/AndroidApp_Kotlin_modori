package com.example.guruapp

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.concurrent.thread
import kotlin.concurrent.timer

//StopWatch의 ListView에 객체를 연결하기 위한 어댑터
class MyAdapter (private val items:MutableList<MyItem>):BaseAdapter() {
    var isRunning : Boolean = false
    val handler = Handler(Looper.getMainLooper())
    var timeValue = 0
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var convertView = p1
        if (convertView == null){ // custom layout을 inflate해서 convertView 참조 획득
            convertView = LayoutInflater.from(p2?.context).inflate(R.layout.subject_layout, p2, false)
        }
        val item : MyItem = items[p0]
        var FloatingActionButton = convertView?.findViewById<FloatingActionButton>(R.id.btnPlay)
        var tvSubject = convertView?.findViewById<TextView>(R.id.tvSubject)
        var tvTime = convertView?.findViewById<TextView>(R.id.tvTime)
        var tvDelete : TextView? = convertView?.findViewById(R.id.btnDelete)


        var timeValue_Item :Int = item.timeValue
        setTextView(timeValue_Item)?.let{
            tvTime!!.text= it
        }
        //타이머 함수
        //timer의 runOnUiThread 함수를 main 외에서 쓰기엔 어려움이 있어 runnable로 대체
        val runnable = object : Runnable{
            override fun run(){
                timeValue++
                item.timeValue = timeValue
                val hour = (timeValue / 60 / 60)
                val minute = ((timeValue / 60) % 60)
                val second = (timeValue % 60)
                item.hour = hour
                item.minute = minute
                item.second = second
                setTextView(timeValue)?.let{
                    tvTime!!.text = it
                }
                handler.postDelayed(this, 1000)
            }
        }
        if(getItem(p0)==null){
            null
        }
        var myItem : MyItem = getItem(p0) as MyItem
        tvSubject?.setText(myItem.subject)
        tvTime?.setText("%02d:%02d:%02d".format(myItem.hour, myItem.minute, myItem.second))

        //재생 버튼이 눌렸을 때
        FloatingActionButton!!.setOnClickListener{
            if(isRunning==true){ //이미 진행중이라면 멈춤
                FloatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                handler.removeCallbacks(runnable) //타이머 멈춤
                item.timeValue = timeValue
            }else { // 멈춘 상태에서 진행중으로 바꿈
                FloatingActionButton.setImageResource(R.drawable.ic_baseline_pause_24)
                timeValue = item.timeValue
                handler.post(runnable)
            }
            isRunning = !isRunning  //진행 상태 바꿈
        }
        //삭제 버튼일 눌렸을 때
        tvDelete!!.setOnClickListener{
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

    override fun getCount(): Int { //아이템 개수 반환
        return items.size
    }
    //텍스트뷰에 넣을 문자열을 지정된 형식에 따라 리턴
    private fun setTextView(timeValue : Int = 0) : String?{
        return if(timeValue< 0)
            null
        else if(timeValue==0){
            "00:00:00"
        }else{
            val hour = (timeValue / 60 / 60)
            val minute = ((timeValue / 60) % 60)
            val second = (timeValue % 60)
            "%1$02d:%2$02d:%3$02d".format(hour, minute, second)
        }
    }

}