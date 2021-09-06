package com.example.guruapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ListVIewAdapter(val context: tel, val telList:ArrayList<ListViewItem>):BaseAdapter(){
    override fun getView(p0: Int, convertView: View?, parent: ViewGroup?): View {
        val view:View=LayoutInflater.from(context).inflate(R.layout.tel_list_item,null)

        val list_title=view.findViewById<TextView>(R.id.list_title)
        val list_tel=view.findViewById<TextView>(R.id.list_tel)

        val tel=telList[p0]
        list_title.text=tel.title
        list_tel.text=tel.number

        return view
    }

    override fun getItem(p0: Int): Any {
       return telList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return telList.size
    }
}