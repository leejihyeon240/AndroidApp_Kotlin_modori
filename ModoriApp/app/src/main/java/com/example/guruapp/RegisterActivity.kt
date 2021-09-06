package com.example.guruapp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.DropBoxManager
import android.provider.ContactsContract
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.isDigitsOnly
import com.example.guruapp.DBManager
import com.example.guruapp.R
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream

class RegisterActivity : AppCompatActivity() {
    lateinit var ImgUserPhoto : ImageView
    lateinit var tvName : TextView
    lateinit var tvMajor : TextView
    lateinit var tvID : TextView
    lateinit var tvPassword : TextView
    lateinit var edtName : EditText
    lateinit var edtID : EditText
    lateinit var edtPassword : EditText
    lateinit var atvMajor : AutoCompleteTextView

    lateinit var radioGroup: RadioGroup
    lateinit var btnCheck : Button
    lateinit var btnRegister : Button

    lateinit var input_ID : String
    lateinit var input_Name : String
    lateinit var input_Password : String
    lateinit var input_Major : String
    lateinit var input_Grade : String

    lateinit var dbManager : DBManager
    lateinit var sqlitedb : SQLiteDatabase

    var check_id : Boolean = false
    var check_space : Boolean = false
    val REQUEST_GALLERY_TAKE = 2
    private val REQ_STORAGE_PERMISSION = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title="회원가입"
        title = ""

        //객체 연결
        ImgUserPhoto = findViewById(R.id.ImgUserPhoto)
        tvName = findViewById(R.id.tvName)
        tvMajor = findViewById(R.id.tvMajor)
        tvID = findViewById(R.id.tvID)
        tvPassword = findViewById(R.id.tvPassword)

        edtName = findViewById(R.id.edtName)
        edtID = findViewById(R.id.edtID)
        edtPassword = findViewById(R.id.edtPassword)
        atvMajor = findViewById<AutoCompleteTextView>(R.id.atvMajor)

        radioGroup = findViewById(R.id.radioGroup)
        btnCheck = findViewById(R.id.btnCheck)
        btnRegister = findViewById(R.id.btnRegister)

        dbManager = DBManager(this, "study", null, 1)

        var majorList = resources.getStringArray(R.array.major_list)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, majorList)
        atvMajor.setAdapter(arrayAdapter)

        ImgUserPhoto.setOnClickListener{
            selectGallery()
        }
        //중복 체크
        btnCheck.setOnClickListener{
            if(edtID.text.toString()=="")
                return@setOnClickListener
            check_id = true
            sqlitedb = dbManager.readableDatabase
            var cursor : Cursor
            cursor = sqlitedb.rawQuery("SELECT * FROM study WHERE id='" + edtID.text.toString() + "';", null)

            //cursor로 다음 객체가 있을 경우 이미 있는 아이디로 판단
            if(cursor.moveToFirst()) {
                check_id = false
                edtID.requestFocus()
                var builder = AlertDialog.Builder(this)
                builder.setMessage("중복되는 아이디입니다.")
                builder.setPositiveButton("확인", null)
                builder.show()
            }else{
                var builder = AlertDialog.Builder(this)
                builder.setMessage("사용 가능한 아이디입니다.")
                builder.setPositiveButton("확인", null)
                builder.show()
                edtID.setEnabled(false);
            }
            cursor.close()
            sqlitedb.close()
        }

        btnRegister.setOnClickListener{
            // 입력받은 값 세팅
            input_Name = edtName.text.toString()
            input_ID = edtID.text.toString()
            input_Password = edtPassword.text.toString()
            input_Major = atvMajor.text.toString()
            when(radioGroup.checkedRadioButtonId){
                R.id.radio_1 -> input_Grade="1학년"
                R.id.radio_2 -> input_Grade="2학년"
                R.id.radio_3 -> input_Grade="3학년"
                R.id.radio_4 -> input_Grade="4학년"
                else -> input_Grade=""
            }

            // 입력이 안된 목록이 있을 경우 그 목록으로 포커스
            check_space = false
            if (input_Name.equals("")){
                edtName.requestFocus()
                check_space = true
            } else if(input_Major.equals("")) {
                atvMajor.requestFocus()
                check_space = true
            } else if(input_ID.equals("")) {
                edtID.requestFocus()
                check_space = true
            } else if(input_Password.equals("")) {
                edtPassword.requestFocus()
                check_space = true
            } else if(input_Grade.equals(""))
                check_space = true

            //패스워드 규칙 검사
            if(input_Password.length < 8 || input_Password.length> 20){
                var builder = AlertDialog.Builder(this)
                builder.setMessage("패스워드를 8자에서 20자 이내로 입력하세요.")
                builder.setPositiveButton("확인", null)
                builder.show()
                return@setOnClickListener
            }else if(input_Password.isDigitsOnly()){
                var builder = AlertDialog.Builder(this)
                builder.setMessage("패스워드를 영문, 숫자를 조합하여 입력하세요.")
                builder.setPositiveButton("확인", null)
                builder.show()
                return@setOnClickListener
            }


            if(check_space){ //입력이 안 된 항목이 있을 경우 대화상자 출력
                var builder = AlertDialog.Builder(this)
                builder.setMessage("모두 입력하세요.")
                builder.setPositiveButton("확인", null)
                builder.show()
                return@setOnClickListener
            } else if(!check_id) {//id 중복 체크가 되지 않았을 경우
                edtID.requestFocus()
                var builder = AlertDialog.Builder(this)
                builder.setMessage("중복된 아이디가 있는지 확인하세요.")
                builder.setPositiveButton("확인", null)
                builder.show()
                return@setOnClickListener
            }
            //데이터베이스에 항목 추가
            sqlitedb = dbManager.writableDatabase

            val image = ImgUserPhoto.drawable

            var p : SQLiteStatement = sqlitedb.compileStatement("INSERT INTO study VALUES (?, ?, ?, ?, ?, ?, ?,?);")
            p.bindString(1, input_Name)
            p.bindString(2, input_Grade)
            p.bindString(3, input_ID)
            p.bindString(4, input_Password)
            p.bindString(5, input_Major)
            p.bindBlob(6, drawableToByteArray(image))
            p.bindLong(7, 0)
            p.bindLong(8,0)
            p.executeInsert()

            Toast.makeText(this,"회원가입 성공", Toast.LENGTH_SHORT).show()

            sqlitedb.close()

            var dbManager_Portfolio : DBManager_Portfolio = DBManager_Portfolio(this, input_ID+ "_portfolio", null, 1)
            dbManager_Portfolio.close()

            var dbManager_Cal : DBManager_Cal = DBManager_Cal(this, input_ID+ "_grade", null, 1)
            dbManager_Cal.close()
            finish()
        }
    }
    //바이너리 데이터인 이미지를 byteArray로 변환해 사용
    private fun drawableToByteArray(drawable : Drawable?) : ByteArray?{
        val bitmap = drawable?.toBitmap()
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        return byteArray
    }

    //이미지를 설정하고자 할 때 권한 설정
    private fun selectGallery(){
        var readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)

        //권한이 거부되어있을 경우 권한 요청
        if(readPermission == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) { // 이전에 거부한 적 있는지 확인
                var dlg = AlertDialog.Builder(this)
                dlg.setTitle("권한이 필요한 이유")
                dlg.setMessage("이미지를 가져오기 위해선 외부 저장소 읽기 권한이 필수로 필요합니다.")
                dlg.setPositiveButton("확인") {dialog, which -> ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQ_STORAGE_PERMISSION)}
                dlg.setNegativeButton("취소", null)
                dlg.show()
            }else{ //권한이 거부된 적 없을 경우(처음으로 권한 요청)
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQ_STORAGE_PERMISSION)
            }
        }else{
            var intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.type="image/*"
            startActivityForResult(intent, REQUEST_GALLERY_TAKE)
        }
    }
    //실제로 이미지를 가져오는 코드
    override fun onActivityResult(requestCode : Int, resultCode: Int, data : Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            2->{
                if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY_TAKE){
                    ImgUserPhoto.setImageURI(data?.data)
                    ImgUserPhoto.clipToOutline = true
                    ImgUserPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }
        }
    }
}