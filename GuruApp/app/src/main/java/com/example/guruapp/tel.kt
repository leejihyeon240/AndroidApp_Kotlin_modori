package com.example.guruapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog

class tel : AppCompatActivity() {

    lateinit var spinner_tel:Spinner
    lateinit var tellistView:ListView

    //spinner항목
    var list_of_spinner= arrayOf("전체","학사지원팀","학생지원팀")

    //전화번호부 리스트
    var telList_All= arrayListOf<ListViewItem>(ListViewItem("학사지원팀(교과)","02-970-5020"),
            ListViewItem("학생지원팀(장학)","02-970-5062"),
            ListViewItem("취업경력개발팀","02-970-5080"),
            ListViewItem("경비실","02-970-1000"),
            ListViewItem("시설관재팀","02-970-5160"),
            ListViewItem("정보전산팀","02-970-5301"),
            ListViewItem("교목실","02-970-5022"),
            ListViewItem("국제교류팀","02-970-5141"),
            ListViewItem("학생상담센터","02-970-5832"),
            ListViewItem("바롬인성교육부","02-970-5257"),
            ListViewItem("기숙사행정실","02-970-7901"),
            ListViewItem("학사지원팀(수강신청)","02-970-5022"),
            ListViewItem("학사지원팀(성적)","02-970-5021"),
            ListViewItem("학사지원팀(휴복학/전과)","02-970-5025"),
            ListViewItem("학생지원팀(국가근로)","02-970-5066"),
            ListViewItem("학생지원팀(증명서발급)","02-970-5064"),
            ListViewItem("학사지원팀(결강사유서)","02-970-5923"))

    var telList_A= arrayListOf<ListViewItem>(ListViewItem("학사지원팀(교과)","02-970-5020"),
            ListViewItem("학사지원팀(수강신청)","02-970-5022"),
            ListViewItem("학사지원팀(성적)","02-970-5021"),
            ListViewItem("학사지원팀(휴복학/전과)","02-970-5025"),
            ListViewItem("학사지원팀(결강사유서)","02-970-5923"))

    var telList_B= arrayListOf<ListViewItem>(ListViewItem("학생지원팀(장학)","02-970-5062"),
            ListViewItem("학생상담센터","02-970-5832"),
            ListViewItem("학생지원팀(국가근로)","02-970-5066"),
            ListViewItem("학생지원팀(증명서발급)","02-970-5064"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tel)
        title = "학교전화부"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        spinner_tel = findViewById(R.id.spinner_tel)
        tellistView = findViewById<ListView>(R.id.tellist)

        spinner_tel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_of_spinner)

        val ListVIewAdapter_All = ListVIewAdapter(this, telList_All)
        val ListVIewAdapter_A = ListVIewAdapter(this, telList_A)
        val ListVIewAdapter_B = ListVIewAdapter(this, telList_B)

        //spinner 선택된 항목에 따라 리스트 분류
        spinner_tel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> {
                        tellistView.adapter = ListVIewAdapter_All
                        //각 리스트를 클릭시 전화걸기 기능
                        tellistView.setOnItemClickListener { adapterView, view, i, l ->
                            when (i) {
                                0 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5020")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                1 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5062")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                2 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5080")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                3 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-1000")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                4 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5160")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                5 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5301")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                6 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5022")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                7 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5141")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                8 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5832")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                9 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5257")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                10 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-7901")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                11 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5022")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                12 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5021")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                13 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5025")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                14 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5066")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                15 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5064")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                16 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5923")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        tellistView.adapter = ListVIewAdapter_A
                        //각 리스트를 클릭시 전화걸기 기능
                        tellistView.setOnItemClickListener { adapterView, view, i, l ->
                            when (i) {
                                0 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5020")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                1 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5022")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                2 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5021")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                3 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5025")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                4 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5923")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                    2->{
                        tellistView.adapter = ListVIewAdapter_B
                        //각 리스트를 클릭시 전화걸기 기능
                        tellistView.setOnItemClickListener { adapterView, view, i, l ->
                            when (i) {
                                0 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5062")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                1 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5832")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                2 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5066")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                                3 -> {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:02-970-5064")
                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    }
                                }
                            }

                        }
                    }
                }
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