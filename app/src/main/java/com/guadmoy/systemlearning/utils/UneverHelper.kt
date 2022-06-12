package com.guadmoy.systemlearning.utils

import android.content.Context
import android.util.Log
import android.util.Log.DEBUG
import com.guadmoy.systemlearning.BuildConfig.DEBUG
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.channels.AsynchronousFileChannel.open
import java.security.AccessControlContext
import java.util.*
import kotlin.collections.ArrayList

object UneverHelper {
    fun getAllCity(context: Context): ArrayList<String> {
        val tempArray = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("uneverRussia.json")
            val size: Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val cityName = jsonObject.names()

            if (cityName != null) {
                for (n in 0 until cityName.length()) {
                    tempArray.add(cityName.getString(n))
                }
            }
        } catch (e: IOException) {
            Log.d("MyLog", "Error $e")

        }
        return tempArray
    }
    fun getAllUnever(city:String,context: Context): ArrayList<String> {
        val tempArray = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("uneverRussia.json")
            val size: Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val uneverName = jsonObject.getJSONArray(city)

                for (n in 0 until uneverName.length()) {
                    tempArray.add(uneverName.getString(n))
            }
        } catch (e: IOException) {
            Log.d("MyLog", "Error $e")

        }
        return tempArray
    }

    fun filterListData(list: ArrayList<String>,text:String?):ArrayList<String>{
        val tempList = ArrayList<String>()
        tempList.clear()
        if(text == null){
            tempList.add("Не найдено")
            return tempList
        }else {
            for (selection: String in list) {
                if (selection.toLowerCase(Locale.ROOT).startsWith(text.toLowerCase(Locale.ROOT))) {
                    tempList.add(selection)

                }
            }
        }
        if(tempList.size == 0) tempList.add("Не найдено")
        return tempList
    }
}