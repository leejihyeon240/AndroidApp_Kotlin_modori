package com.example.guruapp

import com.google.android.material.floatingactionbutton.FloatingActionButton

//스탑워치 listView를 위한 데이터 클래스
data class MyItem (val subject: String?, var hour: Int, var minute: Int, var second: Int, var timeValue: Int = 0)