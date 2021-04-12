package it.prassel.fivedaysforecast.rest

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import it.prassel.fivedaysforecast.model.ForecastResponse
import okhttp3.*
import java.io.IOException

object HttpInvoker {

    private val client = OkHttpClient()

    fun callICAL(context : Context){
        val iCallApi = ICalServerApi.Factory.create(context) ?: return
        val call = iCallApi.getICAL("https://calendar.google.com/calendar/ical/ivan.molella%40gmail.com/public/basic.ics")
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: retrofit2.Call<ResponseBody>?, t: Throwable?) {
            }

            override fun onResponse(call: retrofit2.Call<ResponseBody>?, response: retrofit2.Response<ResponseBody>?) {
                Log.d("HttpInvoker","callICAL resppnse: ${response?.body()?.toString()}")
            }
        });

    }
    fun run(ctx : Context,url: String,callback: (ForecastResponse?) -> Unit) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("-- <HttpInvoker> " + e.message )
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                var json = response.body()?.string()
                println("-- <HttpInvoker> " + json)
                var gson = Gson()
                var forecastResponse : ForecastResponse? = null
                try{
                    forecastResponse = gson.fromJson(json, ForecastResponse::class.java)
                }catch (e : Throwable){
                    e.printStackTrace()
                }
                callback(forecastResponse)
            }
        })
    }

}