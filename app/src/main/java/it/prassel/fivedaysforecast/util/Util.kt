package it.prassel.fivedaysforecast.util

import android.content.Context
import it.prassel.fivedaysforecast.BadContextException
import java.util.*

object Util {

    @Throws(BadContextException::class)
    fun assertContext(context: Context?) {

        if (context == null) {
            throw BadContextException("Invalid Context Found", null)
        }
    }

    fun capitalize(line: String?): String {
        return if (line != null) Character.toUpperCase(line[0]) + line.substring(1) else ""
    }

    fun capitalizeEachWord(source: String?): String {
        if (source != null){
            var result = ""
            val splitString = source.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (target in splitString) {
                result += (Character.toUpperCase(target[0])
                        + target.substring(1) + " ")
            }
            return result.trim { it <= ' ' }
        }else{
            return ""
        }

    }

    fun calendar2String(cal: Calendar): String {

        val month = cal.get(Calendar.MONTH) + 1
        val day_of_month = cal.get(Calendar.DAY_OF_MONTH)
        val hour_of_day = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val second = cal.get(Calendar.SECOND)

        val monthStr = if (month > 9) month.toString() else "0$month"
        val dayOfMontStr = if (day_of_month > 9) day_of_month.toString() else "0$day_of_month"
        val hourOfDayStr = if (hour_of_day > 9) hour_of_day.toString() else "0$hour_of_day"
        val minuteStr = if (minute > 9) minute.toString() else "0$minute"
        val secondStr = if (second > 9) second.toString() else "0$second"

        val sb = StringBuffer()

        sb.append(cal.get(Calendar.YEAR).toString() + "-")
        sb.append(monthStr + "-")
        sb.append(dayOfMontStr + " " )
        sb.append(hourOfDayStr + ":")
        sb.append(minuteStr + ":" )
        sb.append(secondStr)


        return String(sb)

    }

}