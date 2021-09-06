package com.example.guruapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog


class site : AppCompatActivity() {

    var dataArr = arrayOf("사이트 선택", "서울 여자 대학교", "학생 성장 지원 시스템", "위비티 (공모전 사이트)", "캠퍼스픽")

    lateinit var webView : WebView
    lateinit var spinner : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        title="사이트모음"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        spinner = findViewById(R.id.spinner)

        var adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dataArr)

        spinner.adapter = adapter1

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position) {
                    0 -> {
                    }
                    1 -> {
                        webView.loadUrl("http://www.swu.ac.kr")
                    }
                    2 -> {
                        webView.loadUrl("http://eport.swu.ac.kr/cs/lo/login_form.acl")
                    }
                    3 -> {
                        webView.loadUrl("https://www.wevity.com/")
                    }
                    4 -> {
                        webView.loadUrl("https://www.campuspick.com/")
                    }
                }
            }

        }

        webView = findViewById(R.id.webView)
        spinner = findViewById(R.id.spinner)

        webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
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