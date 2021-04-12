package it.prassel.fivedaysforecast.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ListItem : Serializable {

	 @field:SerializedName("dt")
	 var dt: Int? = null

	 @field:SerializedName("dt_txt")
	 var dtTxt: String? = null

	 @field:SerializedName("weather")
	 var weather: MutableList<WeatherItem?>? = null

	 @field:SerializedName("main")
	 var main: Main? = null

	 @field:SerializedName("clouds")
	 var clouds: Clouds? = null

	 @field:SerializedName("sys")
	 var sys: Sys? = null

	 @field:SerializedName("wind")
	 var wind: Wind? = null

	override fun toString(): String {
		return "ListItem(dt=$dt, dtTxt=$dtTxt, weather=$weather, main=$main, clouds=$clouds, sys=$sys, wind=$wind)"
	}


}