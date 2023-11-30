package com.example.a8beats

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class FragmentMessages : Fragment(R.layout.fragment_messages) {
    private lateinit var context_ : Context
    private lateinit var arrayAdapter : ArrayAdapter<String>
    private lateinit var listView : ListView
    private lateinit var messageText : TextView
    private lateinit var queue : RequestQueue

    private val respList = Response.Listener<JSONObject> {
        User.setMessages(it)
        Toast.makeText(context_, context_.getString(R.string.message_deleted), Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
        queue = Volley.newRequestQueue(context_)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.listView)
        messageText = view.findViewById(R.id.messageText)
        arrayAdapter = ArrayAdapter(context_, android.R.layout.simple_list_item_1)
        registerForContextMenu(listView)

        User.msg.observe(viewLifecycleOwner,{
            arrayAdapter.clear()
            for( i in 0 until it.count()){
                val item = it[i]
                val text = getString(R.string.from).plus(item.getString("sender")).plus("\n").plus(getString(R.string.time)).plus(item.getString("date"))
                arrayAdapter.add(text)
            }
            listView.adapter = arrayAdapter
            }
        )

        listView.setOnItemClickListener{ _: AdapterView<*>, _: View, i: Int, _: Long ->
            User.msg.observe(viewLifecycleOwner,{
                if(it.count() > i) {
                    val text = getString(R.string.from)
                        .plus(it[i].getString("sender"))
                        .plus("\n\n")
                        .plus(getString(R.string.to))
                        .plus(it[i].getString("receiver"))
                        .plus("\n\n")
                        .plus(getString(R.string.time))
                        .plus(it[i].getString("date"))
                        .plus("\n\n")
                        .plus(getString(R.string.message))
                        .plus("\n")
                        .plus(it[i].getString("message"))
                    messageText.text = text
                }else{
                    messageText.text = ""
                }
            })
        }

    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        MenuInflater(context_).inflate(R.menu.delete_context_menu, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info =  item.menuInfo as AdapterView.AdapterContextMenuInfo
         when(item.itemId) {
             R.id.delete -> {
                 val selected = info.position
                 queue.add(RequestManager.delMessage(context_, User.getMsg()[selected].getString("id"), User.username, respList))
             }
             else -> {
             }
         }
        return true
    }

}