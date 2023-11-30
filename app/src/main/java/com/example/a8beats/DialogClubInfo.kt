package com.example.a8beats

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import org.json.JSONObject


class DialogClubInfo(private val queue: RequestQueue, private val club: JSONObject) :
    DialogFragment() {

    private lateinit var context_: Context
    private lateinit var textName: TextView
    private lateinit var textAddress: TextView
    private lateinit var textDesc: TextView
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var buttonAddFriend: ImageButton
    private lateinit var ownerText: TextView
    private lateinit var messageText: EditText
    private lateinit var buttonSendMessage: Button

    private val respList = Response.Listener<JSONObject> {
        User.setMessages(it)
        buttonSendMessage.isEnabled = true
        Toast.makeText(context_, context_.getString(R.string.message_sended), Toast.LENGTH_SHORT)
            .show()
    }

    private val respListAddFriend = Response.Listener<JSONObject> {
        if (it.getString("result") == RequestManager.SUCCESS) {
            User.setFriends(it)
            Toast.makeText(context_, context_.getString(R.string.friend_added), Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                context_,
                context_.getString(R.string.already_friends),
                Toast.LENGTH_SHORT
            ).show()
        }
        buttonAddFriend.isEnabled = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_club_info, null, false)
        alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(getString(R.string.club_info))
        alertDialogBuilder.setView(view)
        textName = view.findViewById(R.id.clubName)
        textAddress = view.findViewById(R.id.address)
        textDesc = view.findViewById(R.id.description)

        textName.text = club.getString("name")
        textAddress.text = club.getString("address")
        textDesc.text = club.getString("descr")
        val owner = club.getString("owner")

        if (owner != "none") {

            val ly = view.findViewById<LinearLayout>(R.id.innerLayout)
            view.findViewById<ImageView>(R.id.logo).visibility = View.VISIBLE
            buttonAddFriend = view.findViewById(R.id.addFriendButton)

            if (owner != User.username) {
                buttonAddFriend.visibility = View.VISIBLE
                buttonAddFriend.isClickable = true
            }

            buttonAddFriend.setOnClickListener {
                buttonAddFriend.isEnabled = false
                queue.add(
                    RequestManager.addFriend(
                        context_,
                        owner,
                        User.username,
                        buttonAddFriend,
                        respListAddFriend
                    )
                )
            }

            ly.visibility = View.VISIBLE

            ownerText = view.findViewById(R.id.owner)
            ownerText.text = owner
            ownerText.visibility = View.VISIBLE

            messageText = view.findViewById(R.id.message)
            messageText.isEnabled = true

            buttonSendMessage = view.findViewById(R.id.sendMessageButton)
            buttonSendMessage.isEnabled = true
            buttonSendMessage.setOnClickListener {
                buttonSendMessage.isEnabled = false
                queue.add(
                    RequestManager.sendMessage(
                        context_,
                        owner,
                        messageText.text.toString(),
                        buttonSendMessage,
                        respList
                    )
                )
            }
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int -> }

        return alertDialogBuilder.create()
    }
}