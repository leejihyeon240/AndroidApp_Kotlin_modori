package com.example.guruapp

import android.content.Intent
import android.database.sqlite.SQLiteStatement
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class play : AppCompatActivity() {

    lateinit var btnMusic1 : FloatingActionButton
    lateinit var btnMusic2 : FloatingActionButton
    lateinit var btnMusic3 : FloatingActionButton
    lateinit var btnMusic4 : FloatingActionButton
    lateinit var btnMusic5 : FloatingActionButton
    lateinit var btnMusic6 : FloatingActionButton
    lateinit var webView : WebView

    var urlArray : ArrayList<String> = arrayListOf(
        "https://www.youtube.com/watch?v=bAyyZeAaii4", //빗소리 백색소음
        "https://www.youtube.com/watch?v=WlObjbUPLps", //장작소리 백색소음
        "https://www.youtube.com/watch?v=X66fLliWRgg", //밤의 숲 백색소음
        "https://www.youtube.com/watch?v=yBmPDPCd_ls", //호그와트 시험기간 백색소음
        "https://www.youtube.com/watch?v=pVVINnUhMxg", //연필 소리 백색소음
        "https://www.youtube.com/watch?v=YPKSQxXJPMU" //성균관 백색소음
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title="백색소음"

        //객체 연결
        btnMusic1 = findViewById(R.id.btnMusic1)
        btnMusic2 = findViewById(R.id.btnMusic2)
        btnMusic3 = findViewById(R.id.btnMusic3)
        btnMusic4 = findViewById(R.id.btnMusic4)
        btnMusic5 = findViewById(R.id.btnMusic5)
        btnMusic6 = findViewById(R.id.btnMusic6)
        webView = findViewById(R.id.webView)

        webView.apply{
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }

        //버튼이 눌리면 해당 url로 webView 이동
        webView.loadUrl("https://www.youtube.com/")
        btnMusic1.setOnClickListener{
            webView.loadUrl(urlArray[0])
        }
        btnMusic2.setOnClickListener{
            webView.loadUrl(urlArray[1])
        }
        btnMusic3.setOnClickListener{
            webView.loadUrl(urlArray[2])
        }
        btnMusic4.setOnClickListener{
            webView.loadUrl(urlArray[3])
        }
        btnMusic5.setOnClickListener{
            webView.loadUrl(urlArray[4])
        }
        btnMusic6.setOnClickListener{
            webView.loadUrl(urlArray[5])
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
            R.id.sub1 ->{//관리자 정보 다이어로그 띄우기
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
                email.putExtra(Intent.EXTRA_TEXT, "문의 분류(에러, 요청사항) : \n문의내용:")
                startActivity(email)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}