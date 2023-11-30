package com.example.a8beats

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import org.json.JSONObject

class DialogFriendInfo(val friend: JSONObject) : DialogFragment() {

    private lateinit var context_: Context
    private lateinit var textFriend: TextView
    private lateinit var alertDialogBuilder: AlertDialog.Builder

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = layoutInflater.inflate(R.layout.dialog_friend_info, null, false)
        alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(getString(R.string.friend_info))
        alertDialogBuilder.setView(view)
        textFriend = view.findViewById(R.id.friendText)
        textFriend.text = Utils.friendJsonToString(context_, friend)
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int -> }

        return alertDialogBuilder.create()
    }
}