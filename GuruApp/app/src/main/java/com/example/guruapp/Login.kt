package com.example.guruapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.ActionBar
import java.util.*

class Login : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb:SQLiteDatabase

    lateinit var idEdt:EditText
    lateinit var pwdEdt:EditText
    lateinit var loginBtn:ImageButton
    lateinit var regBtn:ImageButton

    lateinit var str_id : String
    lateinit var str_pwd : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //ActionBar 숨기기
        var actionBar:ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()

        idEdt=findViewById(R.id.idEdt)
        pwdEdt=findViewById(R.id.pwdEdt)
        loginBtn=findViewById(R.id.loginBtn)
        regBtn=findViewById(R.id.regBtn)

        //로그인
        loginBtn.setOnClickListener {

            str_id =idEdt.text.toString()
            str_pwd=pwdEdt.text.toString()

            dbManager=DBManager(this,"study",null,1)
            sqlitedb=dbManager.readableDatabase

            var cursor:Cursor
            cursor=sqlitedb.rawQuery("SELECT * FROM study WHERE id='"+str_id+"';",null)

            if(cursor.moveToNext()){
                if (cursor.getString(cursor.getColumnIndex("password")).toString()== str_pwd) {

                    //알람 매니저로 매일 일정 시간마다 알람(푸시알람)이 울리도록 설정
                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(this, AlarmReceiver::class.java)
                    intent2.putExtra("intent_id", str_id)
                    val pendingIntent = PendingIntent.getBroadcast(this, AlarmReceiver.NOTIFICATION_ID, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
                    //17시 21을 기준으로 하루마다 알람이 울림
                    val repeatInterval : Long = 3*60*1000//AlarmManager.INTERVAL_DAY

                    //알람 등록 ( 6시 10분 )
                    val calendar : GregorianCalendar = GregorianCalendar()
                    calendar.set(GregorianCalendar.HOUR_OF_DAY, 10)
                    calendar.set(GregorianCalendar.MINUTE, 43)
                    if (calendar.before(GregorianCalendar())){
                        calendar.add(GregorianCalendar.DAY_OF_MONTH, 1)
                    }
                    //일정 시간에 계속 반복
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        repeatInterval,
                        pendingIntent
                    )

                    //로그인에 성공해서 메인페이지로 이동
                    val intent=Intent(this,mainpage::class.java)
                    intent.putExtra("intent_id",str_id)
                    startActivity(intent)
                }else{
                    Toast.makeText(applicationContext,"비밀번호가 틀렸습니다.",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(applicationContext,"존재하지 않는 아이디입니다.",Toast.LENGTH_SHORT).show()
            }
        }

        //회원가입
        regBtn.setOnClickListener {
            val intent=Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }



    }
}