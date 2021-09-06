package com.example.guruapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.util.*


class alarm : AppCompatActivity() {

    lateinit var listView : ListView
    lateinit var navigation : BottomNavigationView
    private var alarmItems = mutableListOf<MyAlarmItem>() // 알람

    lateinit var input_id : String
    var alarmTrue : Boolean = false
    var stopTrue : Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        //액션바 로고
        supportActionBar?.apply{
            title=""
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
            setLogo(R.drawable.logo)
        }

        listView = findViewById(R.id.alarmListView)
        //bottomNavigation에서 현재 메뉴를 표시하기 위해 해당 아이템 select
        navigation = findViewById(R.id.navigationView)
        navigation.selectedItemId = R.id.b_alarm

        //intent를 이용해 아이디를 받아오고, 그 아이디를 바탕으로 알람 데이터 얻음
        var intent = getIntent()
        input_id = intent.getStringExtra("intent_id").toString()
        alarmTrue = intent.getBooleanExtra("alarmTrue", false)
        stopTrue = intent.getBooleanExtra("stopTrue", false)

        //액티비티가 실행될 때 데이터 로드
        loadData()

        //알람이 있어서 액티비티가 켜졌다면 알람 객체 추가
        if(alarmTrue){
            //리스트뷰에 아이템 추가
            var date = Date()
            var item = MyAlarmItem(false, date.day, LocalDate.now().toString())
            alarmItems.add(0, item)

            val adapter = MyAdapterAlarm(alarmItems)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
        } else if(stopTrue){  //스탑워치의 목표치를 달성해서 레벨업 알람이 뜰 경우 레벨업 객체 추가
            var date = Date()
            var item = MyAlarmItem(true,  date.day, LocalDate.now().toString())
            alarmItems.add(0, item)

            val adapter = MyAdapterAlarm(alarmItems)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        //하단바
        navigation.setOnNavigationItemSelectedListener {
            saveData()
            when (it.itemId) {
                R.id.b_home -> {
                    val intent= Intent(this,mainpage::class.java)
                    intent.putExtra("intent_id",input_id)
                    startActivity(intent)
                    true
                }
                R.id.b_stopwatch -> {
                    val intent= Intent(this,stopwatch::class.java)
                    intent.putExtra("intent_id",input_id)
                    startActivity(intent)
                    true
                }
                R.id.b_portfolio->{
                    val intent= Intent(this,PortfolioActivity::class.java)
                    intent.putExtra("intent_id",input_id)
                    startActivity(intent)
                    true
                }
                R.id.b_alarm->{
                    true
                }
                else -> true
            }

        }
    }
    //bottomnavigation 설정
//    private fun initNavigationBar(){
//        navigationView.run{
//            setOnNavigationItemSelectedListener {
//                saveData()
//                val nextActivity =
//                    when(it.itemId){
//                        R.id.b_home -> { MainActivity::class.java }
//                        R.id.b_stopwatch -> { stopwatch::class.java }
//                        R.id.b_portfolio -> {PortfolioActivity::class.java }
//                        R.id.b_alarm -> {alarm::class.java}
//                        else -> null
//                    }
//                if ( nextActivity != null){
//                    val intent = Intent(this@alarm, nextActivity)
//                    intent.putExtra("id", "jinwoo1265")
//                    startActivity(intent)
//                }
//                true
//            }
//        }
//    }
    //menu 관련 메소드
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            R.id.sub1 ->{
                //관리자 정보 다이어로그 띄우기
                var builder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.question_dialog, null)

                builder.setView(dialogView)
                builder.setPositiveButton("확인", null)
                builder.show()
            }
            R.id.sub2->{
                saveData()
                var email : Intent = Intent(Intent.ACTION_SEND)
                email.setType("plain/text")
                var address = arrayOf("heyrim2010@naver.com")
                email.putExtra(Intent.EXTRA_EMAIL, address)
                email.putExtra(Intent.EXTRA_SUBJECT, "[문의하기]")
                email.putExtra(Intent.EXTRA_TEXT, "문의 분류(에러, 요청사항) : \n문의내용 : ")
                startActivity(email)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //데이터 저장
    private fun saveData(){
        val sharePreferences = getSharedPreferences("shared preferences "+input_id, Context.MODE_PRIVATE)
        val editor = sharePreferences.edit()
        val gson = Gson()
        val json = gson.toJson(alarmItems)
        editor.putString("alarmList", json)
        editor.apply()
    }
    //데이터 불러오기
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("shared preferences "+input_id, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("alarmList", "")
        val type = object: TypeToken<MutableList<MyAlarmItem>>(){
        }.type

        if(json==null || json==""){
            alarmItems = mutableListOf<MyAlarmItem>()
        }else {
            alarmItems = gson.fromJson(json, type)
            var adapter: MyAdapterAlarm = MyAdapterAlarm(alarmItems)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

    }
}