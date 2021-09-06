package com.example.guruapp

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView


class MyAdapterPortfolio(private val items:MutableList<MyPortfolio>): BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var convertView = p1
        if (convertView == null){ // custom layout을 inflate해서 convertView 참조 획득
            convertView = LayoutInflater.from(p2?.context).inflate(R.layout.portfolio_layout, p2, false)
        }
        val item : MyPortfolio = items[p0]

        var imgPortfolio = convertView?.findViewById<ImageView>(R.id.imgPortfolio)
        var imgColor = convertView?.findViewById<ImageView>(R.id.imgColor)
        var tvTitle = convertView?.findViewById<TextView>(R.id.tvPortfolioTitle)
        var tvPortfolioContent = convertView?.findViewById<TextView>(R.id.tvPortfolioContent)
        var tvPortfolioDate = convertView?.findViewById<TextView>(R.id.tvPortfolioDate)


        imgPortfolio!!.clipToOutline = true

        imgPortfolio?.setImageBitmap(BitmapFactory.decodeByteArray(item.image, 0, item.image!!.size))
        imgColor?.setColorFilter(Color.parseColor(item.color))
        tvTitle!!.text = item.title
        tvPortfolioContent!!.text = item.content
        tvPortfolioDate!!.text = item.startDate + " ~ " + item.lastDate

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