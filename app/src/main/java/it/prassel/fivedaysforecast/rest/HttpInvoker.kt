package it.prassel.fivedaysforecast.rest

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import it.prassel.fivedaysforecast.model.ForecastResponse
import okhttp3.*
import java.io.IOException

object HttpInvoker {

    private val client = OkHttpClient()

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