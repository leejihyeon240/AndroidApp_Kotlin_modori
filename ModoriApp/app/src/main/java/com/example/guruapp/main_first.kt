package com.example.guruapp

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [main_first.newInstance] factory method to
 * create an instance of this fragment.
 */
class main_first : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var str_time by Delegates.notNull<Long>()
    lateinit var fragimg:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            str_time=it.getLong("str_time")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_first, container, false)
    }

    //스탑워치 누적시간(초단위)에 따라 이미지 변경
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var fragimg:ImageView=view.findViewById(R.id.fragimg)
        if(str_time>=500){
            fragimg.setImageResource(R.drawable.lvfive)
        }else if (str_time>=400){
            fragimg.setImageResource(R.drawable.lvfour)
        }else if(str_time>=300){
            fragimg.setImageResource(R.drawable.lvthree)
        }else if(str_time>=200){
            fragimg.setImageResource(R.drawable.lvtwo)
        }else if(str_time>=100){
            fragimg.setImageResource(R.drawable.lvone)
        }else{
            fragimg.setImageResource(R.drawable.lvzero)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment main_first.
         */
        // TODO: Rename and change types and number of parameters
        private const val num="num"
        @JvmStatic
        fun newInstance(param1: Long):main_first{
            return main_first().apply {
                arguments = Bundle().apply {
                    putLong(num, param1)
                }
            }
        }
    }
}