package com.example.a8beats

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelStoreOwner
import com.android.volley.RequestQueue
import com.android.volley.Response
import org.json.JSONException
import org.json.JSONObject

class DialogBandCreate(private val queue : RequestQueue) : DialogFragment() {

    lateinit var context_ : Context
    private val respList = Response.Listener<JSONObject> {
        User.setBands(it)
        Toast.makeText(context_, context_.getString(R.string.band_created_succ), Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
         this.context_ = context
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val alertDialogBuilder = AlertDialog.Builder(activity)
        val view = layoutInflater.inflate(R.layout.dialog_create_band, null , false)

        alertDialogBuilder.setView(view)
        alertDialogBuilder.setTitle(getString(R.string.create_band))
        alertDialogBuilder.setPositiveButton(getString(R.string.create)) { _: DialogInterface, _: Int ->

            val bandName = view.findViewById<EditText>(R.id.create_band_band_name).text
            val bandGenre = view.findViewById<EditText>(R.id.create_band_genre).text
            val bandCountry = view.findViewById<EditText>(R.id.create_band_country).text
            val bandCity = view.findViewById<EditText>(R.id.create_band_city).text
            val role = view.findViewById<EditText>(R.id.create_band_role).text
            try {
                if(bandName.isNotEmpty() and bandGenre.isNotEmpty() and bandCountry.isNotEmpty() and bandCity.isNotEmpty() and role.isNotEmpty() ){
                    queue.add(
                        RequestManager.createBand(
                            context_, role.toString(),
                            bandName.toString(),
                            bandGenre.toString(),
                            bandCountry.toString(),
                            bandCity.toString(),
                            respList
                        )
                    )
                }else{
                    Toast.makeText(context_, getString(R.string.empty_field), Toast.LENGTH_SHORT).show()
                }
            }catch (e: JSONException){
                throw e
            }
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int -> }

        return alertDialogBuilder.create()
    }
}