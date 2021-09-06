package com.example.guruapp

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class cal : AppCompatActivity() {

    lateinit var dbManager: DBManager_Cal
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var lineChart:LineChart

    lateinit var str_id:String
    lateinit var left:Button
    lateinit var right:Button

    lateinit var listView:ListView
    lateinit var tvGrade:TextView

    lateinit var final_cal:TextView
    lateinit var addBtn:Button
    lateinit var applyBtn:Button
    lateinit var resetBtn:Button
    var finals= arrayOf(0F,0F,0F,0F,0F,0F,0F,0F)
    var num:Int=0

    private var items = mutableListOf<MyCalItem>() //(리스트뷰의 내용물)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cal)
        title="학점계산"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        left=findViewById(R.id.left)
        right=findViewById(R.id.right)
        tvGrade=findViewById(R.id.tvGrade)
        final_cal=findViewById(R.id.final_cal)
        lineChart=findViewById(R.id.lineChart)

        var arr = arrayOf(R.id.g1_1, R.id.g1_2, R.id.g2_1, R.id.g2_2, R.id.g3_1, R.id.g3_2, R.id.g4_1, R.id.g4_2)
        var gradeArr= arrayOf("1학년 1학기","1학년 2학기","2학년 1학기","2학년 2학기","3학년 1학기","3학년 2학기","4학년 1학기","4학년 2학기")

        addBtn=findViewById(R.id.addBtn)
        applyBtn=findViewById(R.id.applyBtn)
        resetBtn=findViewById(R.id.resetBtn)

        listView=findViewById(arr[num])
        tvGrade.setText(gradeArr[num])

        val intent = intent
        str_id = intent.getStringExtra("intent_id").toString()

        load()

        left.setOnClickListener {
            listView.setVisibility(View.GONE)
            num--
            if (num<0){
                num=0
            }
            tvGrade.setText(gradeArr[num])
            listView=findViewById(arr[num])
            load()
            listView.setVisibility(View.VISIBLE)
        }
        right.setOnClickListener {
            listView.setVisibility(View.GONE)
            num++
            if (num>7){
                num=7
            }
            tvGrade.setText(gradeArr[num])
            listView=findViewById(arr[num])
            load()
            listView.setVisibility(View.VISIBLE)
        }

        addBtn.setOnClickListener {
            addSubject()
        }

        applyBtn.setOnClickListener {
            apply()
        }

        resetBtn.setOnClickListener {
            reset()
        }
    }

    //listView 관련 메소드
    private fun addSubject(){
        var input_Subject : String =""
        var input_credit:String=""
        var input_score:String=""

        //다이어로그로 과목을 입력받음
        var builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_cal, null)
        val subject = dialogView.findViewById<EditText>(R.id.subject)
        val credit = dialogView.findViewById<EditText>(R.id.credit)
        val score = dialogView.findViewById<EditText>(R.id.score)

        builder.setView(dialogView)
        builder.setPositiveButton("확인"){ dialogInterface, i ->
            input_Subject=subject.text.toString()
            input_credit=credit.text.toString()
            input_score=score.text.toString()

            var item = MyCalItem(input_Subject, input_credit, input_score)
            items.add(item)

            val adapter = MyCalAdapter(items)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
            dbManager = DBManager_Cal(this, str_id+"_grade", null, 1)
            sqlitedb = dbManager.writableDatabase

            var p : SQLiteStatement = sqlitedb.compileStatement("INSERT INTO "+str_id+"_grade VALUES (?, ?, ?, ?);")
            p.bindString(1, num.toString())
            p.bindString(2, subject.text.toString())
            p.bindString(3, credit.text.toString())
            p.bindString(4, score.text.toString())
            p.executeInsert()

            sqlitedb.close()
            dbManager.close()
        }
        builder.setNegativeButton("취소"){dialogInterface, i -> /*아무일도 하지 않음*/}
        builder.show()

    }

    private fun apply(){
        var sum_credit:Float=0F
        var sum_score:Float=0F

        dbManager = DBManager_Cal(this, str_id+"_grade", null, 1)
        sqlitedb = dbManager.readableDatabase

        var cursor:Cursor
        cursor=sqlitedb.rawQuery("SELECT * FROM '"+str_id+"_grade' WHERE num='"+num+"';",null)

        while (cursor.moveToNext()){
            sum_credit+=cursor.getString(cursor.getColumnIndex("credit")).toFloat()
            sum_score+=((cursor.getString(cursor.getColumnIndex("credit"))).toInt()*
                    (cursor.getString(cursor.getColumnIndex("score"))).toFloat())
        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()

        var final=sum_score/sum_credit

        if(sum_credit==0f){
            final_cal.setText("0.0/4.5")
        }else {
            final_cal.setText("%.2f/4.5".format(final))
        }

        finals[num]=final

        val entries = ArrayList<Entry>()
        for (i in 0..7){
            entries.add(Entry(i.toFloat(),finals[i]))
        }

        val vl = LineDataSet(entries, "")
        vl.lineWidth=2f

        vl.fillAlpha=R.color.white
        vl.fillColor=R.color.white

        val xAxis=lineChart.xAxis

        xAxis.setDrawLabels(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f

        val rightYAxis=lineChart.axisRight
        rightYAxis.setDrawLabels(false)

        lineChart.data= LineData(vl)
        lineChart.description.text=""
        lineChart.animateXY(1,1)
        lineChart.invalidate()

    }

    private fun reset(){
        items.clear()
        var adapter :MyCalAdapter = MyCalAdapter(items)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()

        dbManager = DBManager_Cal(this, str_id+"_grade", null, 1)
        sqlitedb = dbManager.writableDatabase
        sqlitedb.execSQL("DELETE FROM " + str_id +"_grade WHERE num='"+num+"';")

        sqlitedb.close()
        dbManager.close()
    }

    private fun load(){

        items.clear()

        var sum_credit:Float=0F
        var sum_score:Float=0F

        dbManager = DBManager_Cal(this, str_id+"_grade", null, 1)
        sqlitedb = dbManager.readableDatabase

        var cursor:Cursor
        cursor=sqlitedb.rawQuery("SELECT * FROM '"+str_id+"_grade' WHERE num='"+num+"';",null)

        while (cursor.moveToNext()){

            var subject=cursor.getString(cursor.getColumnIndex("subject"))
            var credit=cursor.getString(cursor.getColumnIndex("credit"))
            var score=cursor.getString(cursor.getColumnIndex("score"))
            var item = MyCalItem(subject, credit, score)
            sum_credit+=cursor.getString(cursor.getColumnIndex("credit")).toFloat()
            sum_score+=((cursor.getString(cursor.getColumnIndex("credit"))).toInt()*
                    (cursor.getString(cursor.getColumnIndex("score"))).toFloat())
            items.add(item)
        }

        var final=sum_score/sum_credit

        if(sum_credit==0f){
            final_cal.setText("0.0/4.5")
        }else {
            final_cal.setText("%.2f/4.5".format(final))
        }

        val adapter = MyCalAdapter(items)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()

        cursor.close()
        sqlitedb.close()
        dbManager.close()
    }
    //menu 관련 메소드
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home ->{
                finish()
                return true
            }
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
}