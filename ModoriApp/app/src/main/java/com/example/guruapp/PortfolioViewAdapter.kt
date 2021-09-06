package com.example.guruapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.security.AccessController.getContext

class PortfolioViewAdapter(context: main_second, arrayList: ArrayList<HashMap<String,String>>):BaseAdapter() {
    var context=context
    var arrayList=arrayList

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view:View= LayoutInflater.from(p1?.context).inflate(R.layout.viewpager_portfolio_list_item,null)
        val view_lsit_ptitle=view.findViewById<TextView>(R.id.view_lsit_ptitle)
        val view_lsit_pdate=view.findViewById<TextView>(R.id.view_list_pdate)

        val map=arrayList[p0]
        view_lsit_ptitle!!.setText(map.get("title"))
        view_lsit_pdate!!.setText(map.get("date"))

        return view
    }

    override fun getItem(p0: Int): Any {
        return arrayList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return arrayList.size
    }
}