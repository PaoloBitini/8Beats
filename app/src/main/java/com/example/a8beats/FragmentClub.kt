package com.example.a8beats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import com.android.volley.toolbox.Volley


class FragmentClub : Fragment() {
    lateinit var name : TextView
    lateinit var address : TextView
    lateinit var desc : TextView
    lateinit var modify : Button
    lateinit var delete : Button
    lateinit var dialogModify : DialogClubNewDesc
    lateinit var dialogDelete : DialogClubDelete

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_club, container, false)

        name = view.findViewById(R.id.clubName)
        address = view.findViewById(R.id.address)
        desc = view.findViewById(R.id.description)
        modify = view.findViewById(R.id.modifyButton)
        delete = view.findViewById(R.id.deleteButton)

        name.text = User.club.getString("name")
        address.text = User.club.getString("address")
        desc.text = User.club.getString("descr")

        modify.setOnClickListener{
            dialogModify = DialogClubNewDesc(Volley.newRequestQueue(context), parentFragmentManager)
            dialogModify.show(requireActivity().supportFragmentManager, "Modify Description")
        }

        delete.setOnClickListener{
            dialogDelete = DialogClubDelete(Volley.newRequestQueue(context), parentFragmentManager)
            dialogDelete.show(requireActivity().supportFragmentManager, "Delete Club")
        }

        setFragmentResultListener("close_club_fragment") { _: String, _: Bundle ->
            requireActivity().supportFragmentManager.commit {
                remove(this@FragmentClub)
                hide(requireActivity().supportFragmentManager.findFragmentByTag("search")!!)
                hide(requireActivity().supportFragmentManager.findFragmentByTag("board")!!)
                hide(requireActivity().supportFragmentManager.findFragmentByTag("messages")!!)
                show(requireActivity().supportFragmentManager.findFragmentByTag("map")!!)

            }
        }

        setFragmentResultListener("modify_club_fragment") { _: String, _: Bundle ->
            desc.text = User.club.getString("descr")
        }
        return view
    }
}