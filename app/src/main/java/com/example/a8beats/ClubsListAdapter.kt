package com.example.a8beats

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import org.json.JSONObject

class ClubsListAdapter(private val context : Context, private var clubs:  List<JSONObject> ) : BaseAdapter() {

    override fun getCount(): Int {
        return clubs.count()
    }

    override fun getItem(position: Int): Any {
        return clubs[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.style_list_clubs, parent, false)
        val text = view.findViewById<TextView>(R.id.text)
        text.text = clubs[position].getString("name")
            .plus("\n")
            .plus(clubs[position].getString("address"))
            .plus("\n")
            .plus(clubs[position].getString("distance"))
        if (clubs[position].getString("owner") != "none"){
            view.findViewById<ImageView>(R.id.image).visibility = View.VISIBLE
        }
        return view
    }

    fun setList(list : List<JSONObject>){
        clubs = list
    }
}
