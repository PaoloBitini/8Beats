package com.example.a8beats

import androidx.lifecycle.MutableLiveData
import org.json.JSONObject

class User {
    companion object {
        var username = "default"
        var email = "default"
        var token = "default"
        var club = JSONObject()
        var bands = MutableLiveData<ArrayList<JSONObject>>()
        var msg = MutableLiveData<ArrayList<JSONObject>>()
        var friends = MutableLiveData<ArrayList<JSONObject>>()
        var posts = MutableLiveData<ArrayList<JSONObject>>()
        var events = MutableLiveData<ArrayList<JSONObject>>()

        fun setUser(data: JSONObject) {

            username = data.getString("user")
            email = data.getString("email")
            token = data.getString("token")
            club = data.getJSONObject("club")
            msg.value = Utils.fromJsonArrayToList(data.getJSONArray("messages"))
            bands.value = Utils.fromJsonArrayToList(data.getJSONArray("bands"))
            friends.value = Utils.fromJsonArrayToList(data.getJSONArray("friends"))
            posts.value = Utils.fromJsonArrayToList(data.getJSONArray("posts"))
            events.value = Utils.fromJsonArrayToList(data.getJSONArray("events"))
        }
        fun setBands(data: JSONObject) {
            bands.value = Utils.fromJsonArrayToList(data.getJSONArray("bands"))
        }
        fun setMessages(data: JSONObject) {
            msg.value = Utils.fromJsonArrayToList(data.getJSONArray("messages"))
        }
        fun setFriends(data: JSONObject) {
            friends.value = Utils.fromJsonArrayToList(data.getJSONArray("friends"))
        }
        fun addPost(data: JSONObject) {
            val switch = posts.value
            switch!!.add(data)
            posts.value = switch
        }
        fun addEvent(data: JSONObject) {
            val switch = events.value
            switch!!.add(data)
            events.value = switch
        }
        fun delPost(i: Int) {
            val switch = posts.value
            switch!!.removeAt(i)
            posts.value = switch
        }
        fun delEvent(i: Int) {
            val switch = events.value
            switch!!.removeAt(i)
            events.value = switch
        }
        fun getPosts(): ArrayList<JSONObject> {
            return posts.value!!
        }
        fun getEvents(): ArrayList<JSONObject> {
            return events.value!!
        }
        fun getBands(): ArrayList<JSONObject> {
            return bands.value!!
        }
        fun getMsg(): ArrayList<JSONObject> {
            return msg.value!!
        }
        fun getFriends(): ArrayList<JSONObject> {
            return friends.value!!
        }
    }
}
