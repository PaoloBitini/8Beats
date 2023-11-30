package com.example.a8beats

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class DialogClubNewDesc(private var queue: RequestQueue, private val fm : FragmentManager) : DialogFragment() {
    private lateinit var context_ : Context
    private lateinit var newDesc: EditText
    private lateinit var alertDialogBuilder : AlertDialog.Builder
    private val respList = Response.Listener<JSONObject> {
        Toast.makeText(context_, context_.getString(R.string.new_desc_added), Toast.LENGTH_SHORT).show()
        User.club = it.getJSONObject("club")
        fm.setFragmentResult("modify_club_fragment", Bundle())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_change_desc, null, false)
        newDesc = view.findViewById(R.id.newDesc)
        alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(getString(R.string.new_desc))
        alertDialogBuilder.setView(view)
        alertDialogBuilder.setPositiveButton(getString(R.string.change)) { _: DialogInterface, _: Int ->
            if (newDesc.text.isNotEmpty()) {
                queue.add(RequestManager.clubModify(context_, User.username, User.club, newDesc.text.toString(), null, respList))
            }
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int -> }
        return alertDialogBuilder.create()
    }
}