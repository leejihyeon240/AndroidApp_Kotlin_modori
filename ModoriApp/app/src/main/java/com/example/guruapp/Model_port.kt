package com.example.guruapp

import android.graphics.Bitmap

class Model_port {
    private lateinit var image:Bitmap
    private var title:String=""
    private var date:String=""
    private var content:String=""
    private var color:String=""

    constructor(image: Bitmap, title: String, date: String, content: String, color:String) {
        this.image = image
        this.title = title
        this.date=date
        this.content = content
        this.color=color

    }

    fun getImage():Bitmap{
        return image
    }

    fun setImage(image: Bitmap){
        this.image=image
    }

    fun getTitle():String{
        return title
    }

    fun setTitle(title: String){
        this.title=title
    }

    fun getDate():String{
        return date
    }

    fun setDate(date: String){
        this.date=date
    }

    fun getContent():String{
        return content
    }

    fun setContent(content: String){
        this.content=content
    }

    fun getColor():String{
        return color
    }

    fun setColor(color: String){
        this.color=color
    }
}