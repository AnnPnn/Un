package com.anpln.uniboard.utils

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


object DisciplineHelper {
    fun getAllFaculty(context: Context): ArrayList<String> {
        var tempArray = ArrayList<String>()//массив для элементов
        try {
            val inputStream: InputStream = context.assets.open("fd.json")
            val size: Int = inputStream.available()//получение размера исходого файла
            val bytesArray = ByteArray(size)//массив для помещения считанных байтов
            inputStream.read(bytesArray)//запись в массив
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val facultyNames = jsonObject.names()
            if (facultyNames != null) {
                for (n in 0 until facultyNames.length()) {
                    tempArray.add(facultyNames.getString(n))

                }

            }

        } catch (e: IOException) {

        }
        return tempArray
    }

    //функция извлечения элементов из json файла
    //получение списка факультетов
    fun getAllDiscipline(country: String, context: Context): ArrayList<String> {
        var tempArray = ArrayList<String>()//массив для элементов
        try {
            val inputStream: InputStream = context.assets.open("fd.json")
            val size: Int = inputStream.available()//получение размера исходого файла
            val bytesArray = ByteArray(size)//массив для помещения считанных байтов
            inputStream.read(bytesArray)//запись в массив
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val disciplineNames = jsonObject.getJSONArray(country)

            for (n in 0 until disciplineNames.length()) {
                tempArray.add(disciplineNames.getString(n))

            }

        } catch (e: IOException) {

        }
        return tempArray
    }
    //получение списка городов

    fun filterListData(list: ArrayList<String>, searchText: String?): ArrayList<String> {
        val tempList = ArrayList<String>() //временный масиив для франения отфилрованных данных
        tempList.clear()
        if (searchText == null) {
            tempList.add("No result")
            return tempList

        }
        for (selection: String in list) {
            if (selection.toLowerCase(Locale.ROOT)
                    .startsWith(searchText.toLowerCase(Locale.ROOT))
            )
                tempList.add(selection)
        }

        if (tempList.size == 0) tempList.add("No result")
        return tempList
    }

}
//фильтр списка факультетов


