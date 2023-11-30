package com.example.a8beats

import android.content.Context
import android.os.Build
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RequestManager {
    companion object {

        private const val API_URL = "http://192.168.1.81:3000/api"
        const val GET_URL = "$API_URL/auth/get"
        const val REGISTER_URL = "$API_URL/register"
        const val LOGIN_URL = "$API_URL/login"
        const val CREATE_BAND_URL = "$API_URL/auth/bands/create"
        const val JOIN_BAND_URL = "$API_URL/auth/bands/join"
        const val MODIFY_BAND_URL = "$API_URL/auth/bands/modify"
        const val LEAVE_BAND_URL = "$API_URL/auth/bands/leave"
        const val CLAIM_CLUB_URL = "$API_URL/auth/clubs/claim"
        const val DELETE_CLUB_URL = "$API_URL/auth/clubs/delete"
        const val MODIFY_CLUB_URL = "$API_URL/auth/clubs/modify"
        const val SEND_MESSAGE_URL = "$API_URL/auth/message/send"
        const val DEL_MESSAGE_URL = "$API_URL/auth/message/clear"
        const val ADD_FRIEND_URL = "$API_URL/auth/friends/add"
        const val DEL_FRIEND_URL = "$API_URL/auth/friends/del"
        const val ADD_POST_URL = "$API_URL/auth/posts/add"
        const val DEL_POST_URL = "$API_URL/auth/posts/del"
        const val ADD_EVENT_URL = "$API_URL/auth/events/add"
        const val DEL_EVENT_URL = "$API_URL/auth/events/del"
        const val TYPE_BANDS = 1
        const val TYPE_CLUBS = 2
        const val TYPE_POSTS_EVENTS = 3
        const val TYPE_USERS = 4
        const val SUCCESS = "success"


        fun get(context: Context, type: Int, query: String, respList: Response.Listener<JSONObject>): JsonObjectRequest {
            val obj = JSONObject().put("type", type).put("query", query)
            return object : JsonObjectRequest(
                Method.POST, GET_URL, obj, respList,
                { error ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }
        }
        fun login(context: Context, username: String, password: String, but : Button?, respList: Response.Listener<JSONObject>): JsonObjectRequest {

            val jsonObject = JSONObject().put(
                "username",
                username.filter { !it.isWhitespace() }) //remove the whitespace from the string
                .put("psw", password)
            return JsonObjectRequest(
                Request.Method.POST, LOGIN_URL, jsonObject, respList,
                { error ->
                    if(but != null){
                    but.isEnabled = true;
                }
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            )
        }
        fun register(context: Context, user: String, email: String, psw: String, but : Button?, respList: Response.Listener<JSONObject>): JsonObjectRequest {
            val jsonRequest = JSONObject().put("user", user)
                .put("email", email)
                .put("psw", psw)

            return JsonObjectRequest(
                Request.Method.POST, REGISTER_URL, jsonRequest, respList,
                { error ->
                    if(but != null){
                        but.isEnabled = true;
                    }
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()
                })
        }
        fun createBand(context: Context, role: String, bandName: String, bandGenre: String, bandCountry: String, bandCity: String, respList: Response.Listener<JSONObject>): JsonObjectRequest {
            val jsonReq = JSONObject().put("user", User.username).put("role", role)
                .put("name", bandName)
                .put("genre", bandGenre)
                .put("country", bandCountry)
                .put("city", bandCity)

            return object :
                JsonObjectRequest(Method.POST, CREATE_BAND_URL, jsonReq, respList, { error ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }
        }
        fun joinBand(context: Context, bandName: String, role: String, respList: Response.Listener<JSONObject>): JsonObjectRequest {
            val obj =
                JSONObject().put("band", bandName).put("user", User.username).put("role", role)
            return object : JsonObjectRequest(
                Method.POST, JOIN_BAND_URL, obj, respList,
                { error ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }
        }
        fun leaveBand(context: Context, list: ArrayList<String>, respList: Response.Listener<JSONObject>): JsonObjectRequest {
            val jsonReq = JSONObject().put("user", User.username)
            val bands = JSONArray()
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                bands.put(JSONObject().put("band", iterator.next()))
            }
            jsonReq.put("bands", bands)
            return object :
                JsonObjectRequest(Method.POST, LEAVE_BAND_URL, jsonReq, respList, { error ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }
        }
        fun clubClaim(context: Context, name: String, address: String, country: String, lat: Double, lon: Double, respList: Response.Listener<JSONObject>): JsonObjectRequest {
            val req = JSONObject().put("name", name)
                .put("address", address)
                .put("country", country)
                .put("lat", lat)
                .put("lon", lon)
                .put("descr", "no description")
                .put("owner", User.username)

            return object : JsonObjectRequest(Method.POST, CLAIM_CLUB_URL, req, respList, { error ->
                Toast.makeText(
                    context,
                    context.getString(R.string.error_occured) + error.cause,
                    Toast.LENGTH_SHORT
                ).show()

            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }
        }
        fun clubDelete(context: Context, but : Button?, respList: Response.Listener<JSONObject>): JsonObjectRequest {
            val req = JSONObject()
                .put("user", User.username)
                .put("club", User.club.getString("name"))
                .put("country", User.club.getString("country"))

            return object : JsonObjectRequest(
                Method.POST, DELETE_CLUB_URL, req, respList,
                { error ->
                    if(but != null){
                        but.isEnabled = true;
                    }
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }
        }
        fun clubModify(context: Context, user: String, club: JSONObject, newDesc: String, but : Button?, respList: Response.Listener<JSONObject>): JsonObjectRequest {
            val req = JSONObject()
                .put("user", user)
                .put("descr", newDesc)
                .put("club", club.getString("name"))
                .put("country", club.getString("country"))

            return object : JsonObjectRequest(Method.POST, MODIFY_CLUB_URL, req, respList,
                { error ->
                    if(but != null){
                        but.isEnabled = true;
                    }
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun sendMessage(context: Context, receiver: String, message: String, but : Button?, respList: Response.Listener<JSONObject>): JsonObjectRequest {

            val date = LocalDateTime.now()
            val pattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
            val id = date.format(pattern)

            val req = JSONObject()
                .put("id", id)
                .put("sender", User.username)
                .put("receiver", receiver)
                .put("message", message)
                .put("date", date)

            return object : JsonObjectRequest(Method.POST, SEND_MESSAGE_URL, req, respList,
                { error ->
                    if(but != null){
                        but.isEnabled = true;
                    }
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }
        }
        fun delMessage(context: Context, id: String, user: String, respList: Response.Listener<JSONObject>): JsonObjectRequest {

            val message = JSONObject()
                .put("user", user)
                .put("id", id)

            return object : JsonObjectRequest(Method.POST, DEL_MESSAGE_URL, message, respList,
                { error ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }

        }
        fun addFriend(context: Context, friend: String, user: String, but : ImageButton?, respList: Response.Listener<JSONObject>): JsonObjectRequest {
            val req = JSONObject()
                .put("user", user)
                .put("friend", friend)

            return object : JsonObjectRequest(Method.POST, ADD_FRIEND_URL, req, respList,
                { error ->
                    if(but != null){
                        but.isEnabled = true;
                    }
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }
        }
        fun delFriend(context: Context, friend: String, user: String, respList: Response.Listener<JSONObject>): JsonObjectRequest {

            val req = JSONObject()
                .put("user", user)
                .put("friend", friend)

            return object : JsonObjectRequest(Method.POST, DEL_FRIEND_URL, req, respList,
                { error ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }
        }
        fun modifyBandDescription(context: Context, band: String, desc: String, respList: Response.Listener<JSONObject>): JsonObjectRequest {

            val req = JSONObject()
                .put("band", band)
                .put("descr", desc)

            return object : JsonObjectRequest(Method.POST, MODIFY_BAND_URL, req, respList,
                { error ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }

        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun addPost(context: Context, user: String, obj: String, desc: String, but : Button?, respList: Response.Listener<JSONObject>): JsonObjectRequest {

            val date = LocalDateTime.now()
            val pattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
            val id = date.format(pattern)

            val req = JSONObject()
                .put("id", id)
                .put("user", user)
                .put("obj", obj)
                .put("desc", desc)

            return object : JsonObjectRequest(Method.POST, ADD_POST_URL, req, respList,
                { error ->
                    if(but != null){
                        but.isEnabled = true;
                    }
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }

        }
        fun delPost(context: Context, user: String, id: String, respList: Response.Listener<JSONObject>): JsonObjectRequest {

            val req = JSONObject()
                .put("user", user)
                .put("id", id)

            return object : JsonObjectRequest(Method.POST, DEL_POST_URL, req, respList,
                { error ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }

        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun addEvent(context: Context, user: String, date: String, club: String, band: String, desc: String, but : Button?, respList: Response.Listener<JSONObject>): JsonObjectRequest {
            val dateForId = LocalDateTime.now()
            val pattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
            val id = dateForId.format(pattern)

            val req = JSONObject()
                .put("id", id)
                .put("user", user)
                .put("date", date)
                .put("club", club)
                .put("band", band)
                .put("desc", desc)

            return object : JsonObjectRequest(Method.POST, ADD_EVENT_URL, req, respList,
                { error ->
                    if(but != null){
                        but.isEnabled = true;
                    }
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }

        }
        fun delEvent(context: Context, user: String, id: String, respList: Response.Listener<JSONObject>): JsonObjectRequest {

            val req = JSONObject()
                .put("user", user)
                .put("id", id)

            return object : JsonObjectRequest(Method.POST, DEL_EVENT_URL, req, respList,
                { error ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_occured) + error.cause,
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = User.token
                    return headers
                }
            }
        }

    }
}