package com.example.guruapp

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BoardAdapter(context: Context, arrayList: ArrayList<HashMap<String,String>>):BaseAdapter() {

    var context=context
    var arrayList=arrayList

    var tempList=ArrayList(arrayList)

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view=p1
        var holder:ViewHolder

        if (p1==null){
            val Inflater=(context as Activity).layoutInflater
            view=Inflater!!.inflate(R.layout.board_list_item,p2,false)
            holder= ViewHolder()
            holder.mTitle=view!!.findViewById(R.id.blsit_title) as TextView
            holder.mMsg=view!!.findViewById(R.id.blist_msg) as TextView
            view.setTag(holder)
        }
        else{
            holder=view!!.getTag() as ViewHolder
        }
        val map=arrayList.get(p0)
        holder.mTitle!!.setText(map.get("title"))
        holder.mMsg!!.setText(map.get("msg"))

        return view
    }

    override fun getItem(p0: Int): Any {
        return arrayList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
       return arrayList.size
    }

    class ViewHolder{
        var mTitle:TextView?=null
        var mMsg:TextView?=null
    }

    //검색
    fun filter(text:String){
        val text=text!!.toLowerCase(Locale.getDefault())
        arrayList.clear()

        if(text.length==0){
            arrayList.addAll(tempList)
        }else{
            for (i in 0..tempList.size-1){
                if (tempList.get(i).get("title")!!.toLowerCase(Locale.getDefault()).contains(text)){
                    arrayList.add(tempList.get(i))
                }
            }
        }
        notifyDataSetChanged()
    }

}