package com.example.guruapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.viewpager.widget.PagerAdapter

class Adapter_portfolio : PagerAdapter {

    private lateinit var models :List<Model_port>
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var context:Context


    constructor(models: List<Model_port>,context: Context) {
        this.models = models
        this.context = context
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object`)
    }

    override fun getCount(): Int {
        return models.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater= LayoutInflater.from(context)
        var view:View=layoutInflater.inflate(R.layout.item_portfolio,container,false)

        var imageView:ImageView
        var icon:ImageView
        var title:TextView
        var date:TextView
        var content:TextView

        imageView=view.findViewById(R.id.image_port)
        icon=view.findViewById(R.id.icon_port)
        title=view.findViewById(R.id.title_port)
        date=view.findViewById(R.id.date_port)
        content=view.findViewById(R.id.content_port)

        imageView.setImageBitmap(models.get(position).getImage())
        icon.setImageResource(R.drawable.portfolio_icon)
        title.setText(models.get(position).getTitle())
        date.setText(models.get(position).getDate())
        content.setText(models.get(position).getContent())
        icon.setColorFilter(Color.parseColor(models.get(position).getColor()))

        view.setOnClickListener {
            Toast.makeText(context,"리스트에서 수정해주세요.",Toast.LENGTH_SHORT).show()
        }

        container.addView(view,0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

}