package it.prassel.kotlin.vimsmobile.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.StringTokenizer
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Created by ivan on 17/10/16.
 */

object DateUtil {

    private val TAG = "DateUtil"


    internal var mWeekOffset = intArrayOf(1, 0, -1, -2, -3, -4, -5)


    fun daysBetween(startDate: Calendar, endDate: Calendar): Long {

        try {
            startDate.set(Calendar.HOUR_OF_DAY, 0)
            startDate.set(Calendar.MINUTE, 0)
            startDate.set(Calendar.SECOND, 0)

            endDate.set(Calendar.HOUR_OF_DAY, 1)
            endDate.set(Calendar.MINUTE, 0)
            endDate.set(Calendar.SECOND, 0)

            val end = endDate.timeInMillis
            val start = startDate.timeInMillis

            return TimeUnit.MILLISECONDS.toDays(Math.abs(end - start))
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return 0
    }

    fun getFirstCalendarDay(currentCalendar: Calendar): Calendar {

        val localCal = currentCalendar.clone() as Calendar
        val dayOfWeek = localCal.get(Calendar.DAY_OF_WEEK)
        val dateFormat = SimpleDateFormat("EEE")
        val dayOfWeekStr = dateFormat.format(localCal.time)

        val curDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val curDate = curDateFormat.format(localCal.time)

        val weekOffset = mWeekOffset[dayOfWeek - 1]

        localCal.add(Calendar.DAY_OF_MONTH, weekOffset)
        val firstDayStr = DateUtil.formatDate("dd/MM/yyyy", localCal.time)

        val localMonth = localCal.get(Calendar.MONTH)
        val originalMonth = currentCalendar.get(Calendar.MONTH)
        val firstDay = localCal.get(Calendar.DAY_OF_MONTH)

        if (localMonth == originalMonth && firstDay != 1) {
            localCal.add(Calendar.DAY_OF_MONTH, -7)
        }

        Log.v(TAG, "-- First Day Of Week for current date: $curDate dayOfWeekStr: $dayOfWeekStr $dayOfWeek offset: $weekOffset firstDate: $firstDayStr")

        return localCal

    }

    fun formatDateWithDayOfWeek(srcDateFmt: String, srcDate: String): String {

        var retDate: String? = ""

        val dt = SimpleDateFormat(srcDateFmt)
        try {
            val date = dt.parse(srcDate)
            val dt1 = SimpleDateFormat("EEE dd.MM.yyyy")
            retDate = dt1.format(date)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return retDate?.toUpperCase() ?: ""
    }

    fun firstDateOfCurrentMonth(): Calendar {

        val curCal = Calendar.getInstance()
        curCal.set(Calendar.DAY_OF_MONTH, 1)

        return curCal

    }

    fun calendarAtSpecificTime(hour: String): Calendar? {

        val curCal = Calendar.getInstance()

        try {
            val stkz = StringTokenizer(hour, ":")
            val hourElem = stkz.nextToken()
            val minElem = stkz.nextToken()
            curCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourElem))
            curCal.set(Calendar.MINUTE, Integer.parseInt(minElem))
        } catch (e: Throwable) {
            e.printStackTrace()
        }


        return curCal

    }

    fun getMonthKey(date: Calendar?): String? {

        return if (date != null) formatDate("yyyyMM", date.time) else null
    }

    fun dateAtStartMonth(date: Calendar): Calendar {

        val theDate = date.clone() as Calendar

        theDate.set(Calendar.DAY_OF_MONTH, 1)
        theDate.set(Calendar.HOUR_OF_DAY, 0)
        theDate.set(Calendar.MINUTE, 0)
        theDate.set(Calendar.SECOND, 0)

        return theDate
    }

    fun isSameMonth(cal1: Calendar?, cal2: Calendar?): Boolean {
        if (cal1 == null || cal2 == null) {
            return false
        } else {
            if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                return true
            }
        }

        return false
    }

    fun isBetweenDate(start: Calendar?, end: Calendar?, target: Calendar?): Boolean {
        if (start == null || end == null || target == null) {
            return false
        } else {
            val startTime = start.time.time
            val endTime = end.time.time
            val targetTime = target.time.time

            return if (targetTime >= startTime && targetTime <= endTime) {
                true
            } else {
                false
            }
        }
    }

    fun dateDiffInMillis(start: Calendar?, end: Calendar?): Long {
        if (start == null || end == null) {
            return 0
        } else {
            val startTime = start.time.time
            val endTime = end.time.time

            return endTime - startTime
        }
    }

    fun getMonthName(cal: Calendar): String {
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    }

    fun formatDate(srcDateFmt: String, outDateFmt: String, srcDate: String): String {

        var retDate: String? = ""

        val dt = SimpleDateFormat(srcDateFmt)
        try {
            val date = dt.parse(srcDate)
            val dt1 = SimpleDateFormat(outDateFmt)
            retDate = dt1.format(date)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return retDate?.toUpperCase() ?: ""
    }

    fun formatDate(srcDateFmt: String, outDateFmt: String, srcDate: String, tz: TimeZone): String {

        var retDate: String? = ""

        val dt = SimpleDateFormat(srcDateFmt)
        try {
            val date = dt.parse(srcDate)
            val dt1 = SimpleDateFormat(outDateFmt)
            dt1.timeZone = tz
            retDate = dt1.format(date)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return retDate?.toUpperCase() ?: ""
    }

    fun stringDateToCalendar(srcDateFmt: String, srcDate: String?): Calendar? {

        var cal: Calendar? = null

        val dt = SimpleDateFormat(srcDateFmt)
        try {
            val date = dt.parse(srcDate)
            cal = Calendar.getInstance()
            cal!!.time = date
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return cal
    }

    fun todayDateAsString(): String {

        val today = Calendar.getInstance()
        return formatDate("dd/MM/yyyy", today.time)

    }

    fun formatDate(outDateFmt: String, srcDate: Date): String {

        var retDate: String? = ""

        try {
            val dt = SimpleDateFormat(outDateFmt)
            retDate = dt.format(srcDate)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return retDate?.toUpperCase() ?: ""
    }

    fun getTimeFromString(srcDateFmt: String, srcDate: String): Long {

        var retTime: Long = 0

        val dt = SimpleDateFormat(srcDateFmt)
        try {
            val date = dt.parse(srcDate)
            retTime = date.time
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return retTime
    }

    fun addStringHourToCalendarReminder(mCurReminderDate: Calendar, time: String?) {

        var hour = 10

        if (time != null && time.length > 0) {
            val tokenizer = StringTokenizer(time, ":")
            if (tokenizer.hasMoreTokens()) {
                try {
                    val hourStr = tokenizer.nextToken()
                    hour = Integer.parseInt(hourStr)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

            }
        }
        mCurReminderDate.set(Calendar.HOUR_OF_DAY, hour)
        mCurReminderDate.set(Calendar.MINUTE, 0)
    }
}
