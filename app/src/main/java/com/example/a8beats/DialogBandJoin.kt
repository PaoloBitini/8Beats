package com.example.a8beats

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject

class DialogBandJoin(private val queue: RequestQueue) : DialogFragment() {
    private lateinit var context_ : Context
    private lateinit var alertDialogBuilder : AlertDialog.Builder
    private lateinit var searchButton : ImageButton
    private lateinit var searchText : EditText
    private lateinit var listView : ListView
    private lateinit var arrayAdapter : ArrayAdapter<String>
    private lateinit var role : EditText
    private var bands = JSONArray()

    private val respListGet = Response.Listener<JSONObject>  {
        arrayAdapter.clear()
        bands = it.getJSONArray("bands")
        for (i in 0 until bands.length()) {
            val record = bands.getJSONObject(i).getString("name")
                .plus("\n")
                .plus(bands.getJSONObject(i).getString("genre")).plus(", ")
                .plus(bands.getJSONObject(i).getString("city")).plus(" in ")
                .plus(bands.getJSONObject(i).getString("country"))
            arrayAdapter.add(record)
        }
        listView.adapter = arrayAdapter
    }

    private val respListJoin = Response.Listener<JSONObject> {
        if(it.getString("result") == "success") {
            User.setBands(it)
            Toast.makeText(context_,context_.getString(R.string.band_joined), Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context_, context_.getString(R.string.already_a_member), Toast.LENGTH_SHORT).show()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context_ = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_join_band, null , false)

        alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setView(view)
        alertDialogBuilder.setTitle(getString(R.string.join_band))
        searchButton = view.findViewById(R.id.searchButton)
        searchText = view.findViewById(R.id.searchText)
        listView = view.findViewById(R.id.listView)
        arrayAdapter = ArrayAdapter<String>(context_,android.R.layout.simple_list_item_single_choice)
        role = view.findViewById(R.id.role_selection)

        searchButton.setOnClickListener {
            if (searchText.text.isNotEmpty()) {
                queue.add(
                    RequestManager.get(
                        context_,
                        RequestManager.TYPE_BANDS,
                        searchText.text.toString(),
                        respListGet
                    )
                )
            }
        }

        alertDialogBuilder.setPositiveButton(getText(R.string.join)) { _: DialogInterface, _: Int ->

            var selectedItem = ""

            for (i in 0 until listView.count){
                if(listView.isItemChecked(i)){
                    selectedItem = bands.getJSONObject(i).getString("name")
                    break
                }
            }
            if( role.text.isNotEmpty() and selectedItem.isNotEmpty()) {
                queue.add(
                    RequestManager.joinBand(
                        context_,
                        selectedItem,
                        role.text.toString(),
                        respListJoin
                    )
                )
            }else{
                Toast.makeText(context_,getString(R.string.band_role_empty), Toast.LENGTH_SHORT).show()
            }
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int -> }
        return alertDialogBuilder.create()
    }
}