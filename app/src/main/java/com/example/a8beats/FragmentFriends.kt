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

class FragmentFriends : Fragment(R.layout.fragment_friends) {

    private lateinit var context_: Context
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var userAdapter: ArrayAdapter<String>
    private lateinit var listView: ListView
    private lateinit var userList: ListView
    private lateinit var friendText: TextView
    private lateinit var messageText: EditText
    private lateinit var userText: EditText
    private lateinit var messageButton: Button
    private lateinit var searchButton: ImageButton
    private var selectedFriend: JSONObject? = null
    private lateinit var queue: RequestQueue

    private val respListDelFriend = Response.Listener<JSONObject> {
        User.setFriends(it)
        Toast.makeText(context_, context_.getString(R.string.friend_deleted), Toast.LENGTH_SHORT).show()
    }

    private val respListMessages = Response.Listener<JSONObject> {
        messageButton.isEnabled = true
        User.setMessages(it)
        Toast.makeText(context_, context_.getString(R.string.message_sended), Toast.LENGTH_SHORT).show()
    }

    private val respListUsers = Response.Listener<JSONObject> {
        userAdapter.clear()
        val users = Utils.fromJsonArrayToList(it.getJSONArray("users"))
        for (user in users) {
            val userName = user.getString("username")
            if (userName != User.username) {
                userAdapter.add(user.getString("username"))
            }
        }
        userList.adapter = userAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
        queue = Volley.newRequestQueue(context_)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.listView)
        userList = view.findViewById(R.id.userList)
        messageText = view.findViewById(R.id.messageText)
        friendText = view.findViewById(R.id.friendText)
        userText = view.findViewById(R.id.userText)
        messageButton = view.findViewById(R.id.messageButton)
        searchButton = view.findViewById(R.id.searchButton)
        arrayAdapter = ArrayAdapter(context_, android.R.layout.simple_list_item_1)
        userAdapter = ArrayAdapter(context_, android.R.layout.simple_list_item_1)
        registerForContextMenu(listView)

        User.friends.observe(viewLifecycleOwner, {
            arrayAdapter.clear()
            for (i in 0 until it.count()) {
                val text = it[i].getString("name")
                arrayAdapter.add(text)
            }
            listView.adapter = arrayAdapter
        })

        listView.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            User.friends.observe(viewLifecycleOwner, {
                selectedFriend = null
                if (it.count() > i) {
                    selectedFriend = it[i]
                    friendText.text = selectedFriend!!.getString("name")
                } else {
                    friendText.text = ""
                }
            })
        }

//        listView.setOnItemLongClickListener { _: AdapterView<*>, _: View, i: Int, l: Long ->
//            User.friends.observe(viewLifecycleOwner, {
//                selectedFriend = null
//                if (it.count() > i) {
//                    selectedFriend = it[i]
//                    friendText.text = selectedFriend!!.getString("name")
//                    val dialog = DialogFriendInfo(selectedFriend!!)
//                    dialog.show(requireActivity().supportFragmentManager, "friend info")
//
//                } else {
//                    friendText.text = ""
//                }
//            })
//
//            true
//        }

        messageButton.setOnClickListener {
            if (selectedFriend != null && messageText.text.isNotEmpty()) {
                messageButton.isEnabled = false
                queue.add(
                    RequestManager.sendMessage(
                        context_,
                        selectedFriend!!.getString("name"),
                        messageText.text.toString(),
                        messageButton,
                        respListMessages
                    )
                )
            }
        }

        userList.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            val dialog = DialogFriendAdd(queue, userAdapter.getItem(i)!!)
            dialog.show(requireActivity().supportFragmentManager, "add Friend")
        }

        searchButton.setOnClickListener {
            if (userText.text.isNotEmpty()) {
                queue.add(
                    RequestManager.get(
                        context_,
                        RequestManager.TYPE_USERS,
                        userText.text.toString(),
                        respListUsers
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
        MenuInflater(context_).inflate(R.menu.friends_context_menu, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        when (item.itemId) {
            R.id.delete -> {
                val selected = info.position
                queue.add(
                    RequestManager.delFriend(
                        context_,
                        User.getFriends()[selected].getString("name"),
                        User.username,
                        respListDelFriend
                    )
                )
            }
            R.id.info ->{
                val selected = User.getFriends()[info.position]
                friendText.text = selected.getString("name")
                val dialog = DialogFriendInfo(selected)
                dialog.show(requireActivity().supportFragmentManager, "friend info")
            }
            else -> {
            }
        }
        return true
    }

}