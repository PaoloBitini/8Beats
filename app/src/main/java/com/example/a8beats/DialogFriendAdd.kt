package com.example.a8beats

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.android.volley.RequestQueue
import com.android.volley.Response
import org.json.JSONObject

class DialogFriendAdd(val queue: RequestQueue, val name: String) : DialogFragment() {
    private lateinit var context_ : Context
    private lateinit var alertDialogBuilder: AlertDialog.Builder

    private val respList = Response.Listener<JSONObject> {
        val activity = activity
        if (it.getString("result") == RequestManager.SUCCESS) {
            User.setFriends(it)
            if(activity != null && isAdded) {
                Toast.makeText(
                    requireActivity().applicationContext,
                    context_.getString(R.string.friend_added),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            if(activity != null && isAdded) {
                Toast.makeText(
                    requireActivity().applicationContext,
                    context_.getString(R.string.already_friends),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = layoutInflater.inflate(R.layout.dialog_friend_add, null, false)
        alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(getString(R.string.add_friend))
        alertDialogBuilder.setView(view)
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int -> }
        alertDialogBuilder.setPositiveButton(getString(R.string.add)) { _: DialogInterface, _: Int ->
            queue.add(RequestManager.addFriend(context_, name, User.username,null,  respList))
        }

        return alertDialogBuilder.create()
    }
}