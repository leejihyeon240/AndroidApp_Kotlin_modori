package com.example.guruapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.IllegalStateException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.timer


class stopwatch : AppCompatActivity() {
    private var time = 0
    private var timerTask : Timer? = null

    lateinit var tvDate : TextView
    lateinit var tvHour : TextView
    lateinit var tvMinute : TextView
    lateinit var tvSecond : TextView
    lateinit var tvAdd : TextView
    lateinit var listView : ListView
    lateinit var btnReset : Button
    lateinit var progress : ProgressBar
    lateinit var dada : ImageView
    lateinit var goal : ImageView

    private var items = mutableListOf<MyItem>() // 과목, 타이머 리스트(리스트뷰의 내용물)
    var isRunning : Boolean=false
    var sum_time = 0
    var goal_time : Int = 0

    lateinit var dbManager : DBManager
    lateinit var sqlitedb : SQLiteDatabase
    lateinit var input_id : String
    lateinit var name : String

    lateinit var navigation : BottomNavigationView
    var goal_arr : ArrayList<Int> = arrayListOf(
            R.drawable.stopwatch_ob3,
            R.drawable.stopwatch_ob1,
            R.drawable.stopwatch_ob4,
            R.drawable.stopwatch_ob2,
            R.drawable.stopwatch_ob5
    )

    @RequiresApi(Build.VERSION_CODES.O)

    override public fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)

        //액션바 로고
        supportActionBar?.apply{
            title=""
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
            setLogo(R.drawable.logo)
        }
        //객체 연결
        tvDate = findViewById(R.id.tvDate)
        tvHour = findViewById(R.id.tvHour)
        tvMinute = findViewById(R.id.tvMinute)
        tvSecond = findViewById(R.id.tvSecond)
        tvAdd = findViewById(R.id.tvAdd)
        listView = findViewById(R.id.listView)
        btnReset = findViewById(R.id.buttonReset)
        progress = findViewById(R.id.progress)
        dada = findViewById(R.id.dada)
        goal = findViewById(R.id.goal)

        //bottomNavigation에서 현재 메뉴를 표시하기 위해 해당 아이템 select
        navigation = findViewById(R.id.navigationView)
        navigation.selectedItemId = R.id.b_stopwatch

        //intent를 이용해 아이디를 받아오고, 그 아이디를 바탕으로 데이터베이스 정보 얻음
        var intent = getIntent()
        input_id = intent.getStringExtra("intent_id").toString()

        //db를 통해서 현재 목표 goal을 불러오기 위함
        dbManager = DBManager(this, "study", null, 1)
        sqlitedb = dbManager.readableDatabase
        try{
            var cursor : Cursor = sqlitedb.rawQuery("SELECT * FROM study WHERE id = '" + input_id + "';", null)

            if(cursor.moveToNext()){
                goal_time = cursor.getLong(cursor.getColumnIndex("goal_time")).toInt()
            }
            cursor.close()
        }catch (exception : IllegalStateException){
            Log.i("No date", exception.toString())
        }

        //현재 goal 목표는 5개 뿐이므로 500을 넘을 경우 goal_time을 0으로 초기화시켜준다(인덱스 에러 방지)
        if(goal_time >= 500){ goal_time = 0 }

        goal.setImageResource(goal_arr[goal_time/100])
        sqlitedb.close()

        //액티비티가 실행될 때 데이터 로드
        loadData()
        progress.setProgress(sum_time)
        dada.x = (sum_time*10-30).toFloat()

        //네비게이션바의 클릭 리스너 정의
//        initNavigationBar()

        var adapter :MyAdapter = MyAdapter(items)
        listView.adapter= adapter

        //날짜 정보 원하는 형식으로 입력
        var formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        tvDate.text="${LocalDate.now().format(formatter)}"

        //추가하기 버튼을 눌렀을 때 리스트뷰에 항목 추가
        tvAdd.setOnClickListener{
            addSubject()
        }

        //리스트뷰 클릭 리스너
        listView.setOnItemClickListener { parent, view, position, id ->

            if (isRunning == false) {
                updateData()
                sum_time -= items[position].timeValue
                start(items[position])
                isRunning = !isRunning
            } else {
                pause(items[position])
                isRunning = !isRunning
            }
        }
        //초기화 버튼을 눌렀을 경우, 총합 타이머 초기화 및 items 초기화
        btnReset.setOnClickListener {
            tvHour.text = "00H"
            tvMinute.text = "00M"
            tvSecond.text = "00"
            for (i in 0 until adapter.count){
                items[i].hour=0
                items[i].minute=0
                items[i].second=0
                items[i].timeValue=0
            }
            items.clear()
            sum_time=0
            var adapter :MyAdapter = MyAdapter(items)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
            progress.setProgress(0)
            pause_goal()
        }
        //하단바
        navigation.setOnNavigationItemSelectedListener {
            //데이터 저장
                sqlitedb = dbManager.writableDatabase
                saveDataNavigate()
                if(isRunning){
                    pause_goal()
                }
            when (it.itemId) {
                R.id.b_home -> {
                    val intent= Intent(this,mainpage::class.java)
                    intent.putExtra("intent_id",input_id)
                    startActivity(intent)
                    true
                }
                R.id.b_stopwatch -> {
                    true
                }
                R.id.b_portfolio->{
                    val intent= Intent(this,PortfolioActivity::class.java)
                    intent.putExtra("intent_id",input_id)
                    startActivity(intent)
                    true
                }
                R.id.b_alarm->{
                    val intent= Intent(this,alarm::class.java)
                    intent.putExtra("intent_id",input_id)
                    intent.putExtra("stopTrue", false)
                    intent.putExtra("alarmTrue", false)
                    startActivity(intent)
                    true
                }
                else -> true
            }

        }
    }
    //뒤로가기 버튼 비활성화를 위한 메소드 오버라이드
    override fun onBackPressed() {
        //super.onBackPressed()
    }
    //스톱워치 기능 동작 메소드
    private fun start(item: MyItem){
        timerTask = timer(period = 100){
            val temp = sum_time + time*100 + item.timeValue
            val hour = temp /60/60
            val minute = (temp / 60) %60
            val sec = temp % 60
            runOnUiThread{
                tvHour.text="%02dH".format(hour)
                tvMinute.text="%02dM".format(minute)
                tvSecond.text="%02d".format(sec)

                //프로그래스바 설정
                progress.setProgress(temp)
                dada.x = (temp*10-30).toFloat()
                //목표치를 달성했을 때 목표 이미지 수정 및 초기화
                if (temp>=100){
                    var adapter : MyAdapter = MyAdapter(items)
                    //스톱워치와 진행상황 초기화
                    tvHour.text = "00H"
                    tvMinute.text = "00M"
                    tvSecond.text = "00"
                    for (i in 0 until adapter.count){
                        items[i].hour=0
                        items[i].minute=0
                        items[i].second=0
                        items[i].timeValue=0
                    }
                    items.clear()
                    sum_time=0
                    adapter.notifyDataSetChanged()
                    progress.setProgress(0)
                    dada.x = 5.0F

                    //목표치 증가
                    goal_time += 100

                    //데이터를 저장하고 레벨업을 알리기 위해 알람창으로 이동
                    sqlitedb = dbManager.writableDatabase
                    saveDataNavigate()

                    //현재 goal 목표는 5개 뿐이므로 500을 넘을 경우 goal_time을 0으로 초기화시켜준다(인덱스 에러 방지)
                    if(goal_time >= 500){ goal_time = 0 }
                    goal.setImageResource(goal_arr[goal_time/100])
                    pause_goal()

                    var intent2 = Intent(this@stopwatch, alarm::class.java)
                    intent2.putExtra("intent_id", input_id)
                    intent2.putExtra("stopTrue", true)
                    intent2.putExtra("alarmTrue", false)
                    startActivity(intent2)
                    return@runOnUiThread
                }
            }
        }
    }
    //타이머를 멈춤
    private fun pause(item: MyItem) {
        timerTask?.cancel()
        item.hour = item.hour + time / 60/60
        item.minute = item.minute + (time/60)%60
        item.second = item.second + time%60
    }

    //목표를 달성했을 경우 타이머를 멈춤
    private fun pause_goal(){
        timerTask?.cancel()
    }

    //listView 관련 메소드
    private fun addSubject(){
        var input_Subject : String =""
        //다이어로그로 과목을 입력받음
        var builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_input_subject, null)
        val dialogText = dialogView.findViewById<EditText>(R.id.message)

        builder.setView(dialogView)
        builder.setPositiveButton("확인"){
            dialogInterface, i -> input_Subject=dialogText.text.toString()
            var item = MyItem(input_Subject, 0, 0, 0)
            items.add(item)

            val adapter = MyAdapter(items)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        builder.setNegativeButton("취소"){dialogInterface, i -> /*아무일도 하지 않음*/}
        builder.show()
    }

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
            R.id.sub2->{ //암묵적 인텐트를 사용해 이메일로 넘어감(이때 형식 정해줌)
                sqlitedb = dbManager.writableDatabase
                saveDataNavigate()
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
        val sharePreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor = sharePreferences.edit()
        val gson = Gson()
        val json = gson.toJson(items)
        editor.putString("list", json)
        editor.putInt("goal_time", sum_time)
        editor.apply()
    }

    //데이터 불러오기
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("list", "")
        val type = object: TypeToken<MutableList<MyItem>>(){
        }.type

        if(json==null || json==""){
            items = mutableListOf<MyItem>()
        }else{
            items = gson.fromJson(json, type)
            var adapter :MyAdapter = MyAdapter(items)
            adapter.notifyDataSetChanged()
            updateData()
        }
    }

    //items의 개수만큼 초를 더해서 총합 타이머를 갱신
    private fun updateData(){
        sum_time = 0
        for ( i in 0 until items.size){
            sum_time += items[i].timeValue
        }
        tvHour.text="%02dH".format(sum_time/60/60)
        tvMinute.text="%02dM".format((sum_time/60)%60)
        tvSecond.text="%02d".format(sum_time%60)
    }

    //네비게이션으로 액티비티 이동할 때 데이터를 저장하는 용도
    private fun saveDataNavigate(){
        var adapter :MyAdapter = MyAdapter(items)
        listView.adapter = adapter
        saveData()

        var p : SQLiteStatement = sqlitedb.compileStatement("UPDATE study SET time = ?, goal_time = ? WHERE id = ?;")
        p.bindLong(1, sum_time.toLong())
        p.bindLong(2, goal_time.toLong())
        p.bindString(3, input_id)
        p.executeInsert()
    }
}
