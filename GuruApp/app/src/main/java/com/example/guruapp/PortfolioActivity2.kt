package com.example.guruapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.util.setVisibility
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.ByteArrayOutputStream
import java.util.*


class PortfolioActivity2 : AppCompatActivity() {
    lateinit var spinner : Spinner
    lateinit var listView : ListView
    lateinit var btnAdd : Button
    lateinit var navigation : BottomNavigationView

    lateinit var dbManager : DBManager_Portfolio
    lateinit var sqlitedb : SQLiteDatabase
    lateinit var input_id : String

    lateinit var dialogImage : ImageView

    val REQUEST_GALLERY_TAKE = 2
    private val REQ_STORAGE_PERMISSION = 1000
    lateinit var input_colorHax : String

    private var items = mutableListOf<MyPortfolio>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio2)

        //액션바 로고
        supportActionBar?.apply{
            title=""
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
            setLogo(R.drawable.logo)
        }

        //객체 연결
        spinner = findViewById(R.id.spinner_view)
        listView = findViewById(R.id.listViewPortfolio)
        btnAdd = findViewById(R.id.btnAdd)

        //bottomNavigation에서 현재 메뉴를 표시하기 위해 해당 아이템 select
        navigation = findViewById(R.id.navigationView)
        navigation.selectedItemId = R.id.b_portfolio

        //하단바
        navigation.setOnNavigationItemSelectedListener {
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

        //intent를 이용해 아이디를 받아오고, 그 아이디를 바탕으로 데이터베이스 정보 얻음
        var intent = getIntent()
        input_id = intent.getStringExtra("intent_id").toString()

        //해당 id의 portfolio 데이터베이스를 불러옴
        dbManager = DBManager_Portfolio(this, input_id+"_portfolio", null, 1)

        //네비게이션바의 클릭 리스너 정의
//        initNavigationBar()

        //액티비티가 시작될 때 이미 있는 포트폴리오 내용 로드
        loadPortfolio()

        //스피너 문자열 아이템을 배열로 받아옴
        val spinner_items = resources.getStringArray(R.array.spinner_list)

        //스피너에 어댑터 연결 및
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinner_items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(1)

        //스피너 아이템 선택 리스너
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when(p2){
                    0->{
                        //TODO("스와이프뷰로 보기")
                        val intent = Intent(this@PortfolioActivity2, PortfolioActivity::class.java)
                        intent.putExtra("intent_id",input_id)
                        startActivity(intent)
                        return
                    }
                    1->{ // 리스트뷰로 보기
                        return
                    }
                    else -> {
                        Toast.makeText(this@PortfolioActivity2, "설정된게 없음", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //추가하기 버튼을 눌렀을 때 리스트뷰에 항목 추가
        btnAdd.setOnClickListener{
            addPortfolio()
        }

        listView.setOnItemClickListener{ parent, view, position, id ->
            viewPortfolio(items[position], position)
        }
        listView.setOnItemLongClickListener{ parent, view, position, id ->
            deletePortfolio(items[position], position)

            //길게 누르면 삭제 되고, 눌러서 크게 볼 때 수정되는게 맞지 않을까..?
        }

    }
    //listView 관련 메소드(항목 추가)
    private fun addPortfolio(){
        var builder = AlertDialog.Builder(this)

        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_input_portfolio, null)
        dialogImage = dialogView.findViewById<ImageView>(R.id.imgInputPortfolio)
        val dialogTitle = dialogView.findViewById<EditText>(R.id.edtTitle)
        val dialogStartDate = dialogView.findViewById<Button>(R.id.btnStartDate)
        val dialogLastDate = dialogView.findViewById<Button>(R.id.btnLastDate)
        val dialogColor = dialogView.findViewById<Button>(R.id.btnColor)
        val dialogContent = dialogView.findViewById<EditText>(R.id.edtContent)


        dialogStartDate.setOnClickListener{
            showTimeDialog(dialogStartDate)
        }
        dialogLastDate.setOnClickListener{
            showTimeDialog(dialogLastDate)
        }
        dialogColor.setOnClickListener{
            showColorDialog(dialogColor)
        }
        dialogImage.setOnClickListener{
            selectGallery()
        }

        builder.setView(dialogView)
        builder.setPositiveButton("확인"){
                dialogInterface, i ->
            //입력받지 않은 값이 있을 경우 저장하지 않는다.
            if(dialogImage.drawable == null || dialogTitle.text.equals("") || dialogContent.text.equals("")
                || dialogStartDate.text.equals("") || dialogLastDate.text.equals("")){
                Toast.makeText(this, "입력하지 않은 값이 있어 저장되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            //아이디_portfolio 데이터베이스 파일을 만듦
            sqlitedb = dbManager.writableDatabase

            var bitmap : Bitmap = (dialogImage.drawable).toBitmap()
            var baos : ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            //아이디_portfolio 데이터베이스에 데이터 추가
            var b : SQLiteStatement = sqlitedb.compileStatement("INSERT INTO " + input_id +"_portfolio VALUES (?, ?, ?, ?, ?, ?);")
            b.bindBlob(1, baos.toByteArray())
            b.bindString(2, dialogTitle.text.toString())
            b.bindString(3, dialogContent.text.toString())
            b.bindString(4, input_colorHax)
            b.bindString(5, dialogStartDate.text.toString())
            b.bindString(6, dialogLastDate.text.toString())
            b.executeInsert()

            //리스트뷰에 아이템 추가
            var item = MyPortfolio(baos.toByteArray(), dialogTitle.text.toString(), dialogContent.text.toString(),
                input_colorHax, dialogStartDate.text.toString(), dialogLastDate.text.toString())
            items.add(item)

            val adapter = MyAdapterPortfolio(items)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()

        }
        builder.setNegativeButton("취소"){dialogInterface, i -> /*아무일도 하지 않음*/}
        builder.show()
    }

    private fun showTimeDialog(tv : TextView){ //데이트피커 출력
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var pickerDialog : DatePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view, year, month, day ->
            tv.text = "%02d.%02d.%02d".format(year, month+1, day)
        }, year, month, day)

        pickerDialog.show()
    }
    //color picker를 구현할 때 build.gradle에 추가할 항목이 있음
    private fun showColorDialog(tv : TextView){

        ColorPickerDialog
            .Builder(this)                    // Pass Activity Instance
            .setTitle("Pick Theme")              // Default "Choose Color"
            .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
            .setColorListener { color, colorHex ->
                tv.setBackgroundColor(color)
                input_colorHax = colorHex
            }
            .show()
    }

    //리스트뷰의 함목을 클릭하면 내용을 볼 수 있게
    private fun viewPortfolio(item : MyPortfolio, position : Int){
        var builder = AlertDialog.Builder(this)

        val dialogView = layoutInflater.inflate(R.layout.view_portfolio, null)
        val viewImage = dialogView.findViewById<ImageView>(R.id.viewImage)
        val viewTitle = dialogView.findViewById<TextView>(R.id.viewTitle)
        val viewDate = dialogView.findViewById<TextView>(R.id.viewDate)
        val viewEditContent = dialogView.findViewById<EditText>(R.id.viewEditContent)
        val viewContent = dialogView.findViewById<TextView>(R.id.viewContent)
        val btnChange = dialogView.findViewById<Button>(R.id.btnChange)

        viewImage?.setImageBitmap(BitmapFactory.decodeByteArray(item.image, 0, item.image!!.size))
        viewTitle.setText(item.title)
        viewDate.setText(item.startDate + " ~ " + item.lastDate)
        viewContent.setText(item.content)

        btnChange.setOnClickListener{
            if(btnChange.text.equals("수정")){ //수정 버튼이 눌렸을 때
                viewEditContent.setVisibility(View.VISIBLE)
                viewContent.setVisibility(View.INVISIBLE)
                viewEditContent.setText(viewContent.text.toString())
                btnChange.setText("적용")
            } else{ //적용 버튼이 눌렸을 때
                viewEditContent.setVisibility(View.INVISIBLE)
                viewContent.setVisibility(View.VISIBLE)
                viewContent.setText(viewEditContent.text.toString())
                btnChange.setText("수정")
            }
        }
        builder.setView(dialogView)
        builder.setPositiveButton("확인"){
                dialogInterface, i ->

            //수정중에 확인을 누른 경우 적용되지 않는다.
            if(!btnChange.text.toString().equals("수정")){
                Toast.makeText(this, "아직 수정중입니다. 입력 사항을 적용해주십시오.", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            //입력받지 않은 값이 있을 경우 저장하지 않는다.
            if(viewImage.drawable == null || viewTitle.text.equals("") || viewContent.text.equals("") || viewDate.text.equals("") ){
                Toast.makeText(this, "입력하지 않은 값이 있어 저장되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            //아이디_portfolio 데이터베이스 파일을 만듦
            sqlitedb = dbManager.writableDatabase

            var bitmap : Bitmap = (viewImage.drawable).toBitmap()
            var baos : ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            //아이디_portfolio 데이터베이스에 데이터 추가
            var b : SQLiteStatement = sqlitedb.compileStatement("UPDATE " + input_id +"_portfolio SET content = ? WHERE title = ?;")
            b.bindString(1, viewContent.text.toString())
            b.bindString(2, viewTitle.text.toString())
            b.executeInsert()

            //리스트뷰에 아이템 추가
            var item = MyPortfolio(item.image, item.title, viewContent.text.toString(), item.color, item.startDate, item.lastDate)
            items.removeAt(position)
            items.add(position, item)

            val adapter = MyAdapterPortfolio(items)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()

        }
        builder.setNegativeButton("취소"){dialogInterface, i -> /*아무일도 하지 않음*/}
        builder.show()

    }
    //포트폴리오 삭제
    private fun deletePortfolio(item:MyPortfolio,position: Int): Boolean {
        var builder=AlertDialog.Builder(this)

        val dialogView=layoutInflater.inflate(R.layout.custom_dialog_delete_portfolio,null)
        val viewImage=dialogView.findViewById<ImageView>(R.id.viewImage)
        val viewTitle=dialogView.findViewById<TextView>(R.id.viewTitle)
        val viewDate=dialogView.findViewById<TextView>(R.id.viewDate)
        val viewContent=dialogView.findViewById<TextView>(R.id.viewContent)

        viewImage?.setImageBitmap(BitmapFactory.decodeByteArray(item.image, 0, item.image!!.size))
        viewTitle.setText(item.title)
        viewDate.setText(item.startDate + " ~ " + item.lastDate)
        viewContent.setText(item.content)

        builder.setView(dialogView)
        builder.setPositiveButton("삭제"){ dialogInterface, i ->
            sqlitedb = dbManager.writableDatabase
            sqlitedb.execSQL("DELETE FROM "+input_id+"_portfolio WHERE title='"+viewTitle.text.toString()+"';")
            items.removeAt(position)
            val adapter = MyAdapterPortfolio(items)
            listView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        builder.setNegativeButton("취소"){dialogInterface,i->  }
        builder.show()

        return true
    }

    //액티비티가 시작될 때 이미 있는 데이터베이스에서 리스트를 가져와서 리스트뷰에 보여줌
    private fun loadPortfolio(){
        sqlitedb = dbManager.readableDatabase
        var cursor : Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM '" +  input_id + "_portfolio';", null)
        while(cursor.moveToNext()){
            var image : ByteArray = cursor.getBlob(cursor.getColumnIndex("image"))
            var title : String = cursor.getString(cursor.getColumnIndex("title"))
            var content : String = cursor.getString(cursor.getColumnIndex("content"))
            var startDate : String = cursor.getString(cursor.getColumnIndex("startDate"))
            var lastDate : String = cursor.getString(cursor.getColumnIndex("lastDate"))
            var color : String = cursor.getString(cursor.getColumnIndex("color"))

            var item : MyPortfolio = MyPortfolio(image, title, content, color, startDate, lastDate)
            items.add(item)
        }
        val adapter = MyAdapterPortfolio(items)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()

        cursor.close()
        sqlitedb.close()
    }

    //bottomnavigation 설정
//    private fun initNavigationBar(){
//        navigationView.run{
//            setOnNavigationItemSelectedListener {
//                val nextActivity =
//                    when(it.itemId){
//                        R.id.b_home -> { MainActivity::class.java }
//                        R.id.b_stopwatch -> { stopwatch::class.java }
//                        R.id.b_portfolio -> { PortfolioActivity::class.java }
//                        R.id.b_alarm -> {
//                            alarm::class.java }
//                        else -> null
//                    }
//                if ( nextActivity != null){
//                    val intent = Intent(this@PortfolioActivity2, nextActivity)
//                    intent.putExtra("intent_id", input_id)
//                    intent.putExtra("stopTrue", false)
//                    intent.putExtra("alarmTrue", false)
//                    startActivity(intent)
//                }
//                true
//            }
//        }
//    }
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
                    dialogImage.setImageURI(data?.data)
                    dialogImage.clipToOutline = true
                    dialogImage.scaleType = ImageView.ScaleType.CENTER_CROP
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