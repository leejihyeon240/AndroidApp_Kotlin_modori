package com.example.guruapp

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

class MyCalAdapter(private val items:MutableList<MyCalItem>): BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var convertView = p1
        if (convertView == null){ // custom layout을 inflate해서 convertView 참조 획득
            convertView = LayoutInflater.from(p2?.context).inflate(R.layout.item_cal, p2, false)
        }
        val item : MyCalItem = items[p0]

        var tvSubject = convertView?.findViewById<TextView>(R.id.subject)
        var tvCredit = convertView?.findViewById<TextView>(R.id.credit)
        var tvScore = convertView?.findViewById<TextView>(R.id.score)

        tvSubject!!.text = item.subject
        tvCredit!!.text = item.credit
        tvScore!!.text = item.score

        return convertView!!
    }

    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}