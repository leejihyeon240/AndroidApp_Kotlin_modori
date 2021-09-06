package com.example.guruapp

import android.animation.ArgbEvaluator
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.viewpager.widget.ViewPager
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class PortfolioActivity : AppCompatActivity() {

    lateinit var spinner : Spinner
    lateinit var dialogImage : ImageView
    val REQUEST_GALLERY_TAKE = 2
    private val REQ_STORAGE_PERMISSION = 1000
    lateinit var input_colorHax : String

    lateinit var p_title:String
    lateinit var p_content:String
    lateinit var p_color:String
    lateinit var p_startDate:String
    lateinit var p_lastDate:String
    lateinit var p_image: ByteArray
    lateinit var dbDBManager_Portfolio:DBManager_Portfolio

    lateinit var viewPager_port:ViewPager
    lateinit var adapterPortfolio: Adapter_portfolio
    lateinit var models: List<Model_port>
    lateinit var navigation: BottomNavigationView
    lateinit var dbManager_portfolio: DBManager_Portfolio
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var addBtn:Button
    lateinit var input_id : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)
        title=""

        //액션바 로고
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
            setLogo(R.drawable.logo)
        }

        var cardView: CardView?=findViewById(R.id.cardView)
        addBtn=findViewById(R.id.addBtn)

        //bottomNavigation에서 현재 메뉴를 표시
        navigation = findViewById(R.id.navigation)
        navigation.selectedItemId=R.id.b_portfolio

        spinner = findViewById(R.id.spinner_view)

        //포트폴리오 내용 불러오기
        load()


        var intent = getIntent()
        input_id = intent.getStringExtra("intent_id").toString()

        //스피너 문자열 아이템을 배열로 받아옴
        val spinner_items = resources.getStringArray(R.array.spinner_list)

        //스피너에 어댑터 연결 및
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinner_items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)

        //스피너 아이템 선택 리스너
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when(p2){
                    0->{
                        //TODO("스와이프뷰로 보기")

                        return
                    }
                    1->{ // 리스트뷰로 보기
                        val intent = Intent(this@PortfolioActivity, PortfolioActivity2::class.java)
                        intent.putExtra("intent_id",input_id)
                        startActivity(intent)
                        return
                    }
                    else -> {
                        Toast.makeText(this@PortfolioActivity, "설정된게 없음", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //포트폴리오 추가하기
        addBtn.setOnClickListener {
            addPortfolio()
        }


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
                var builder = androidx.appcompat.app.AlertDialog.Builder(this)
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

    //데이터베이스에 있는 내용을 불러와 각 카드뷰에 저장하는 함수
    private fun load(){
        models= ArrayList<Model_port>()
        val intent = intent
        var str_id = intent.getStringExtra("intent_id").toString()

        dbDBManager_Portfolio = DBManager_Portfolio(this, str_id+"_portfolio", null, 1)
        sqlitedb = dbDBManager_Portfolio.readableDatabase
        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM "+str_id+"_portfolio;", null)

        while(cursor.moveToNext()) {
            p_title = cursor.getString(cursor.getColumnIndex("title")).toString()
            p_content = cursor.getString(cursor.getColumnIndex("content")).toString()
            p_color = cursor.getString(cursor.getColumnIndex("color")).toString()
            p_startDate = cursor.getString(cursor.getColumnIndex("startDate")).toString()
            p_lastDate = cursor.getString(cursor.getColumnIndex("lastDate")).toString()
            p_image = cursor.getBlob(cursor.getColumnIndex("image"))

            var bm: Bitmap
            bm= BitmapFactory.decodeByteArray(p_image,0, p_image!!.size)
            (models as ArrayList<Model_port>).add(Model_port(bm,p_title,p_startDate+"~"+p_lastDate,p_content,p_color))
        }
        cursor.close()
        sqlitedb.close()
        dbDBManager_Portfolio.close()

        adapterPortfolio= Adapter_portfolio(models,this)
        viewPager_port=findViewById(R.id.viewPager_port)
        viewPager_port.adapter=adapterPortfolio
        viewPager_port.setPadding(130,0,130,0)
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
                    dialogImage.setImageURI(data?.data)
                    dialogImage.clipToOutline = true
                    dialogImage.scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }
        }
    }

    private fun showTimeDialog(tv : TextView){ //데이트피커 출력
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var pickerDialog : DatePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ view, year, month, day ->
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


    //cardView 관련 메소드(항목 추가)
    private fun addPortfolio(){
        var builder = AlertDialog.Builder(this)

        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_input_portfolio, null)
        dialogImage = dialogView.findViewById<ImageView>(R.id.imgInputPortfolio)
        val dialogTitle = dialogView.findViewById<EditText>(R.id.edtTitle)
        val dialogStartDate = dialogView.findViewById<Button>(R.id.btnStartDate)
        val dialogLastDate = dialogView.findViewById<Button>(R.id.btnLastDate)
        val dialogColor = dialogView.findViewById<Button>(R.id.btnColor)
        val dialogContent = dialogView.findViewById<EditText>(R.id.edtContent)

        val intent = intent
        var str_id = intent.getStringExtra("intent_id").toString()


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
            sqlitedb = dbDBManager_Portfolio.writableDatabase

            var bitmap : Bitmap = (dialogImage.drawable).toBitmap()
            var baos : ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            //아이디_portfolio 데이터베이스에 데이터 추가
            var b : SQLiteStatement = sqlitedb.compileStatement("INSERT INTO " + str_id +"_portfolio VALUES (?, ?, ?, ?, ?, ?);")
            b.bindBlob(1, baos.toByteArray())
            b.bindString(2, dialogTitle.text.toString())
            b.bindString(3, dialogContent.text.toString())
            b.bindString(4, input_colorHax)
            b.bindString(5, dialogStartDate.text.toString())
            b.bindString(6, dialogLastDate.text.toString())
            b.executeInsert()

            //ByteArray 비트맵으로 변환
            var bm: Bitmap
            bm= BitmapFactory.decodeByteArray(baos.toByteArray(),0, baos.toByteArray()!!.size)

            //카드뷰에 아이템 추가
            (models as ArrayList<Model_port>).add(Model_port(bm,dialogTitle.text.toString(),dialogStartDate.text.toString()+"~"+dialogLastDate.text.toString(),dialogContent.text.toString(),input_colorHax))
            adapterPortfolio= Adapter_portfolio(models,this)
            viewPager_port=findViewById(R.id.viewPager_port)
            viewPager_port.adapter=adapterPortfolio
            viewPager_port.setPadding(130,0,130,0)
            adapterPortfolio.notifyDataSetChanged()

        }
        builder.setNegativeButton("취소"){dialogInterface, i -> /*아무일도 하지 않음*/}
        builder.show()
    }

    //카드뷰 함목을 클릭하면 내용을 볼 수 있게
    public fun viewPortfolio(item: Model_port, position: Int){

        var builder = AlertDialog.Builder(this)

        val dialogView = layoutInflater.inflate(R.layout.view_portfolio, null)
        val viewImage = dialogView.findViewById<ImageView>(R.id.viewImage)
        val viewTitle = dialogView.findViewById<TextView>(R.id.viewTitle)
        val viewDate = dialogView.findViewById<TextView>(R.id.viewDate)
        val viewEditContent = dialogView.findViewById<EditText>(R.id.viewEditContent)
        val viewContent = dialogView.findViewById<TextView>(R.id.viewContent)
        val btnChange = dialogView.findViewById<Button>(R.id.btnChange)

        models= ArrayList<Model_port>()
        val intent = intent
        var str_id = intent.getStringExtra("intent_id").toString()

        viewImage?.setImageBitmap(item.getImage())
        viewTitle.setText(item.getTitle())
        viewDate.setText(item.getDate())
        viewContent.setText(item.getContent())

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
            sqlitedb = dbDBManager_Portfolio.writableDatabase

            var bitmap : Bitmap = (viewImage.drawable).toBitmap()
            var baos : ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            //아이디_portfolio 데이터베이스에 데이터 추가
            var b : SQLiteStatement = sqlitedb.compileStatement("UPDATE " + str_id +"_portfolio SET content = ? WHERE title = ?;")
            b.bindString(1, viewContent.text.toString())
            b.bindString(2, viewTitle.text.toString())
            b.executeInsert()


            //카드뷰에 아이템 추가
            (models as ArrayList<Model_port>).add(Model_port(item.getImage(),item.getTitle(),item.getDate(),viewContent.text.toString(),input_colorHax))
            (models as ArrayList<Model_port>).removeAt(position)
            adapterPortfolio= Adapter_portfolio(models,this)
            viewPager_port=findViewById(R.id.viewPager_port)
            viewPager_port.adapter=adapterPortfolio
            viewPager_port.setPadding(130,0,130,0)
            adapterPortfolio.notifyDataSetChanged()

        }
        builder.setNegativeButton("취소"){dialogInterface, i -> /*아무일도 하지 않음*/}
        builder.show()

    }

//    //포트폴리오 리스트 삭제
//    private fun deletePortfolio(item:MyPortfolio,position: Int){
//        var builder=AlertDialog.Builder(this)
//
//        val dialogView=layoutInflater.inflate(R.layout.custom_dialog_delete_portfolio,null)
//        val viewImage=dialogView.findViewById<ImageView>(R.id.viewImage)
//        val viewTitle=dialogView.findViewById<TextView>(R.id.viewTitle)
//        val viewDate=dialogView.findViewById<TextView>(R.id.viewDate)
//        val viewContent=dialogView.findViewById<TextView>(R.id.viewContent)
//
//        viewImage?.setImageBitmap(BitmapFactory.decodeByteArray(item.image, 0, item.image!!.size))
//        viewTitle.setText(item.title)
//        viewDate.setText(item.startDate + " ~ " + item.lastDate)
//        viewContent.setText(item.content)
//
//        builder.setView(dialogView)
//        builder.setPositiveButton("삭제"){ dialogInterface, i ->
//            sqlitedb = dbManager.writableDatabase
//            sqlitedb.execSQL("DELETE FROM "+input_id+"_portfolio WHRE title="+viewTitle.text.toString()+";")
//            items.removeAt(position)
//            val adapter = MyAdapterPortfolio(items)
//            listView.adapter = adapter
//            adapter.notifyDataSetChanged()
//        }
//        builder.setNegativeButton("취소"){dialogInterface,i->  }
//        builder.show()
//    }

}
