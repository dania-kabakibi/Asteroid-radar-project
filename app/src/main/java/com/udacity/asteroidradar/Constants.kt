package com.udacity.asteroidradar

import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

object Constants {
    const val API_QUERY_DATE_FORMAT = "YYYY-MM-dd"
    const val DEFAULT_END_DATE_DAYS = 7
    val START_DATE = getNextSevenDaysFormattedDates().get(index = 0)
    val END_DATE = getNextSevenDaysFormattedDates().get(index = getNextSevenDaysFormattedDates().lastIndex)
    const val API_KEY = "Pkw1MKy8JT3BPpDPd91Eic2bMMNrs3RVivRAmJW7"
    const val BASE_URL = "https://api.nasa.gov/"
}

fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.getDefault())
        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }
    return formattedDateList
}