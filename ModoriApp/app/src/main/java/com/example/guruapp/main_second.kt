package com.example.guruapp

import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [main_second.newInstance] factory method to
 * create an instance of this fragment.
 */
class main_second : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var str_id:String=""
    val p_items=ArrayList<HashMap<String,String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            str_id=it.getString("str_id").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_main_second, container, false)

        val dbManager_p = DBManager_Portfolio(activity!!,str_id+"_portfolio",null,1)
        val sqlitedb = dbManager_p.readableDatabase
        var cursor:Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM "+str_id+"_portfolio;", null)
        var title= ArrayList<String>()
        var date= ArrayList<String>()
        while (cursor.moveToNext()){
            title.add(cursor.getString(cursor.getColumnIndex("title")).toString())
            date.add(cursor.getString(cursor.getColumnIndex("date")).toString())
        }
        cursor.close()
        sqlitedb.close()
        dbManager_p.close()

        var hashMap: HashMap<String, String> = HashMap<String, String>()

        for (i in 0..title.size-1){
            hashMap=HashMap<String,String>()
            hashMap.put("title",title[i])
            hashMap.put("date",date[i])
            p_items.add(hashMap)
        }

        val adapter=PortfolioViewAdapter(this,p_items)
        var p_list: ListView =view.findViewById(R.id.viewpager_list)
        p_list.adapter=adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment main_second.
         */
        // TODO: Rename and change types and number of parameters
        private const val num="num"
        @JvmStatic
        fun newInstance(param1: String):main_second{
            return main_second().apply {
                arguments = Bundle().apply {
                    putString(num, param1)
                }
            }
        }
    }
}