package com.example.a8beats

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject


class SharedDataViewModel : ViewModel() {
    val data = MutableLiveData<MutableList<JSONObject>>()
    val events = MutableLiveData<MutableList<JSONObject>>()
    val posts = MutableLiveData<MutableList<JSONObject>>()

    fun setData (list : List<JSONObject>) {
        data.value = list.toMutableList()
    }
    fun setEvents(list : MutableList<JSONObject>){
        events.value = list
    }
    fun setPosts(list : MutableList<JSONObject>){
        posts.value = list
    }
    fun addPost(data: JSONObject){
        val switch = posts.value
        switch!!.add(data)
        posts.value = switch
    }
    fun addEvent(data: JSONObject){
        val switch = events.value
        switch!!.add(data)
        events.value = switch
    }
    fun delPost(i: Int){
        val switch = posts.value
        switch!!.removeAt(i)
        posts.value = switch
    }
    fun delEvent(i: Int){
        val switch = events.value
        switch!!.removeAt(i)
        events.value = switch
    }

}


