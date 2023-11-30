package com.example.a8beats

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
//import com.google.android.gms.common.util.Strings
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DialogBandLeave(private val queue : RequestQueue): DialogFragment() {
    lateinit var context_ : Context
    lateinit var listView: ListView
    private val respList = Response.Listener<JSONObject> {
        User.setBands(it)
        Toast.makeText(context_, context_.getString(R.string.band_left), Toast.LENGTH_LONG).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context_ = context
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val alertDialogBuilder = AlertDialog.Builder(activity)
        val view = layoutInflater.inflate(R.layout.dialog_leave_band, null , false)
        alertDialogBuilder.setView(view)
        alertDialogBuilder.setTitle(getString(R.string.leave_band))
        try {

            listView = view.findViewById(R.id.leave_band_list)
            val arrayAdapter = ArrayAdapter<String>(context_,android.R.layout.simple_list_item_multiple_choice)
            val bands = User.getBands()
            for(i in 0 until bands.count()){
                arrayAdapter.add( bands[i].getString("band"))
            }
            listView.adapter = arrayAdapter

        }catch (e: JSONException){
            throw e
        }

        alertDialogBuilder.setPositiveButton(getString(R.string.leave)
        ) { _: DialogInterface, _: Int ->

            val bands = arrayListOf<String>()
            for(i in 0 until listView.count ){
                if(listView.isItemChecked(i)) {
                    bands.add(listView.getItemAtPosition(i).toString())
                }
            }
            if (bands.count() > 0) {
                queue.add(RequestManager.leaveBand(context_, bands, respList))
            }else{
                Toast.makeText(context_, getString(R.string.one_band_selected), Toast.LENGTH_SHORT).show()
            }
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->
        }

        return alertDialogBuilder.create()
    }
}