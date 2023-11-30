package com.example.a8beats

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class FragmentBands : Fragment(R.layout.fragment_bands) {
    private lateinit var context_: Context
    private lateinit var arrayAdapterSearch: ArrayAdapter<String>
    private lateinit var arrayAdapterYBands: ArrayAdapter<String>
    private lateinit var listViewSearch: ListView
    private lateinit var listViewYBands: ListView
    private lateinit var searchButton: ImageButton
    private lateinit var searchText: EditText
    private lateinit var bandText: TextView
    private lateinit var queue: RequestQueue
    private lateinit var dialogModify: DialogBandNewDesc
    private var selectedBand: JSONObject? = null
    private var bandsSearch = ArrayList<JSONObject>()
    private val respListGet = Response.Listener<JSONObject> {
        arrayAdapterSearch.clear()
        bandsSearch = ArrayList()
        val bandRes = it.getJSONArray("bands")
        for (i in 0 until bandRes.length()) {
            bandsSearch.add(bandRes.getJSONObject(i))
        }

        for (elem in bandsSearch) {
            val record = elem.getString("name")
                .plus("\n")
                .plus(elem.getString("genre")).plus(", ")
                .plus(elem.getString("city")).plus(" in ")
                .plus(elem.getString("country"))
            arrayAdapterSearch.add(record)
        }
        listViewSearch.adapter = arrayAdapterSearch
    }

    private val respListMyBands = Response.Listener<JSONObject> {
        bandText.text= Utils.showBand(context_, it.getJSONArray("bands").getJSONObject(0))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
        queue = Volley.newRequestQueue(context_)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listViewSearch = view.findViewById(R.id.bandsResult)
        listViewYBands = view.findViewById(R.id.yourBands)
        bandText = view.findViewById(R.id.showBand)
        searchText = view.findViewById(R.id.searchText)
        searchButton = view.findViewById(R.id.searchButton)
        arrayAdapterSearch = ArrayAdapter(context_, android.R.layout.simple_list_item_1)
        arrayAdapterYBands = ArrayAdapter(context_, android.R.layout.simple_list_item_1)
        registerForContextMenu(listViewYBands)

        User.bands.observe(viewLifecycleOwner, {
            arrayAdapterYBands.clear()
            for (i in 0 until it.count()) {
                val text = it[i].getString("band")
                arrayAdapterYBands.add(text)
            }
            listViewYBands.adapter = arrayAdapterYBands
        })

        listViewSearch.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            selectedBand = null
            if (bandsSearch.count() > i) {
                selectedBand = bandsSearch[i]
                bandText.text = Utils.showBand(context_, selectedBand!!)
            } else {
                bandText.text = ""
            }
        }

        listViewYBands.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            queue.add(
                RequestManager.get(
                    context_,
                    RequestManager.TYPE_BANDS,
                    arrayAdapterYBands.getItem(i)!!,
                    respListMyBands
                )
            )
        }

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
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        MenuInflater(context_).inflate(R.menu.bands_context_menu, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        when (item.itemId) {
            R.id.modify -> {
                val selected = info.position
                dialogModify = DialogBandNewDesc(queue, User.getBands()[selected].getString("band"))
                dialogModify.show(requireActivity().supportFragmentManager, "Modify Description")
            }
            else -> {
            }
        }
        return true
    }
}