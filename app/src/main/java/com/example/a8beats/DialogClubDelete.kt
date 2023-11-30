package com.example.a8beats

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class DialogClubDelete(private var queue: RequestQueue, private val fm: FragmentManager) : DialogFragment() {
    private lateinit var context_ : Context
    private lateinit var alertDialogBuilder : AlertDialog.Builder

    private val respList = Response.Listener<JSONObject> {
        Toast.makeText(context_, context_.getString(R.string.club_deleted), Toast.LENGTH_SHORT).show()
        User.club = JSONObject()
        fm.setFragmentResult("close_club_fragment", Bundle())
     }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context_ = context
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(getString(R.string.unbind_club))
        alertDialogBuilder.setView(R.layout.dialog_delete_club)
        alertDialogBuilder.setPositiveButton(getString(R.string.unbind)) { _: DialogInterface, _: Int ->
            queue.add(RequestManager.clubDelete(context_, null, respList))
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)){ _: DialogInterface, _: Int -> }

        return alertDialogBuilder.create()
    }
}