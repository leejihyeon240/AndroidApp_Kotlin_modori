package com.example.guruapp

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.relex.circleindicator.CircleIndicator
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class mainpage : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var str_id:String
    lateinit var str_image: ByteArray
    lateinit var str_name:String
    lateinit var str_major:String
    var str_time:Long=0

    lateinit var stdImage:ImageView
    lateinit var stdName:TextView
//    lateinit var viewPager: ViewPager

    lateinit var main_back:LinearLayout
    lateinit var main_text:ImageView

    lateinit var siteBtn:LinearLayout
    lateinit var calBtn:LinearLayout
    lateinit var playBtn:LinearLayout
    lateinit var telBtn:LinearLayout

    lateinit var b_search:SearchView
    lateinit var b_list:ListView

    lateinit var logout:Button
    lateinit var indicator:CircleIndicator

    lateinit var navigation:BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainpage)

        title = ""

        b_search=findViewById(R.id.b_search)
        b_list=findViewById(R.id.b_list)

        //액션바 로고
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
            setLogo(R.drawable.logo)
        }

        stdImage = findViewById(R.id.stdImage)
        stdName = findViewById(R.id.stdName)

        siteBtn = findViewById(R.id.siteBtn)
        calBtn = findViewById(R.id.calBtn)
        playBtn = findViewById(R.id.playBtn)
        telBtn = findViewById(R.id.telBtn)
        logout = findViewById(R.id.logout)
//        viewPager = findViewById(R.id.view)
//        indicator = findViewById(R.id.indicator)

        main_back=findViewById(R.id.main_back)
        main_text=findViewById(R.id.main_text)

        //bottomNavigation에서 현재 메뉴를 표시
        navigation = findViewById(R.id.navigation)
        navigation.selectedItemId=R.id.b_home

        //개인정보
        val intent = intent
        str_id = intent.getStringExtra("intent_id").toString()
        dbManager = DBManager(this, "study", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM study WHERE id='" + str_id + "';", null)

        if (cursor.moveToNext()) {
            str_image = cursor.getBlob(cursor.getColumnIndex("image"))
            str_name = cursor.getString(cursor.getColumnIndex("name")).toString()
            str_major = cursor.getString(cursor.getColumnIndex("major")).toString()
            str_time = cursor.getLong(cursor.getColumnIndex("goal_time"))
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        if (str_time>=500.toLong()){
            main_back.setBackgroundResource(R.drawable.lvfive)
        }else if (str_time>=400.toLong()){
            main_back.setBackgroundResource(R.drawable.lvfour)
        }else if(str_time>=300.toLong()){
            main_back.setBackgroundResource(R.drawable.lvthree)
        }else if(str_time>=200.toLong()){
            main_back.setBackgroundResource(R.drawable.lvtwo)
        }else if(str_time>=100.toLong()){
            main_back.setBackgroundResource(R.drawable.lvone)
        }else{
            main_back.setBackgroundResource(R.drawable.lvzero)
        }

        var date = Date()

        if(date.day%3==0){
            main_text.setImageResource(R.drawable.main_bubble1)
        }else if(date.day%3==1){
            main_text.setImageResource(R.drawable.main_bubble2)
        }else{
            main_text.setImageResource(R.drawable.main_bubble3)
        }



        // 이미지,이름,학과 설정
        //bytrarray bitmap으로 변환
        var bm: Bitmap
        bm= BitmapFactory.decodeByteArray(str_image,0, str_image!!.size)
        stdImage.setImageBitmap(bm)

        stdName.text=str_name+"|"+str_major

        //viewpager
//        setDataAtFragment1(main_first(), str_time)
//        setDataAtFragment2(main_second(),str_id)
//        viewPager.adapter = MyPagerAdapter(supportFragmentManager)
//        viewPager.offscreenPageLimit = 1
//        indicator.setViewPager(viewPager)

        //버튼 클릭시
        siteBtn.setOnClickListener {
            val intent = Intent(this, site::class.java)
            startActivity(intent)
        }

        calBtn.setOnClickListener {
            val intent = Intent(this, cal::class.java)
            intent.putExtra("intent_id",str_id)
            startActivity(intent)
        }

        playBtn.setOnClickListener {
            val intent = Intent(this, play::class.java)
            startActivity(intent)
        }

        telBtn.setOnClickListener {
            val intent = Intent(this, tel::class.java)
            startActivity(intent)
        }

        //로그아웃 클릭시
        logout.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        //게시판
        val title=arrayOf(getString(R.string.b_1title),getString(R.string.b_2title),getString(R.string.b_3title),getString(R.string.b_4title),getString(R.string.b_5title))
        val msg=arrayOf(getString(R.string.b_1msg),getString(R.string.b_2msg),getString(R.string.b_3msg),getString(R.string.b_4msg),getString(R.string.b_5title))

        val b_items=ArrayList<HashMap<String,String>>()
        var hashMap: HashMap<String, String> = HashMap<String, String>()

        for (i in 0..title.size-1){
            hashMap=HashMap<String,String>()
            hashMap.put("title", title[i].toString())
            hashMap.put("msg", msg[i].toString())
            b_items.add(hashMap)
        }

        val boardAdapter=BoardAdapter(this,b_items)
        b_list.adapter=boardAdapter

        b_list.setOnItemClickListener { adapterView, view, i, l ->
            when(i){
                0 -> showAlert1()
                1 -> showAlert2()
                2 -> showAlert3()
                3 -> showAlert4()
                4 -> showAlert5()
            }
        }

        b_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val text=p0
                boardAdapter.filter(text.toString())
                return false
            }

        })

        //하단바
        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.b_home -> {
                    true
                }
                R.id.b_stopwatch -> {
                    val intent=Intent(this,stopwatch::class.java)
                    intent.putExtra("intent_id",str_id)
                    startActivity(intent)
                    true
                }
                R.id.b_portfolio->{
                    val intent=Intent(this,PortfolioActivity::class.java)
                    intent.putExtra("intent_id",str_id)
                    startActivity(intent)
                    true
                }
                R.id.b_alarm->{
                    val intent=Intent(this,alarm::class.java)
                    intent.putExtra("intent_id",str_id)
                    intent.putExtra("stopTrue", false)
                    intent.putExtra("alarmTrue", false)
                    startActivity(intent)
                    true
                }
                else -> true
            }

        }
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
            R.id.sub2->{
                var email : Intent = Intent(Intent.ACTION_SEND)
                email.setType("plain/text")
                var address = arrayOf("heyrim2010@naver.com")
                email.putExtra(Intent.EXTRA_EMAIL, address)
                email.putExtra(Intent.EXTRA_SUBJECT, "[문의하기]")
                email.putExtra(Intent.EXTRA_TEXT, "문의 분류(에러, 요청사항) : \n문의내용")
                startActivity(email)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //시간 값 전달
    fun setDataAtFragment1(fragment: Fragment, time: Long){
        val bundle=Bundle()
        bundle.putLong("str_time",time)
        fragment.arguments=bundle
    }
    fun setDataAtFragment2(fragment: Fragment, id: String){
        val bundle=Bundle()
        bundle.putString("str_id",id)
        fragment.arguments=bundle
    }

    //게시판 팝업
    private fun showAlert1(){
        AlertDialog.Builder(this)
                .setMessage(R.string.b_1msg)
                .setPositiveButton("확인"){dialogInterface, i ->  }
                .show()
    }

    private fun showAlert2(){
        AlertDialog.Builder(this)
                .setMessage(R.string.b_2msg)
                .setPositiveButton("확인"){dialogInterface, i ->  }
                .show()
    }

    private fun showAlert3(){
        AlertDialog.Builder(this)
                .setMessage(R.string.b_3msg)
                .setPositiveButton("확인"){dialogInterface, i ->  }
                .show()
    }

    private fun showAlert4(){
        AlertDialog.Builder(this)
                .setMessage(R.string.b_4msg)
                .setPositiveButton("확인"){dialogInterface, i ->  }
                .show()
    }

    private fun showAlert5(){
        AlertDialog.Builder(this)
                .setMessage(R.string.b_5msg)
                .setPositiveButton("확인"){dialogInterface, i ->  }
                .show()
    }
}