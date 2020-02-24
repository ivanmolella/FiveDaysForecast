package it.prassel.fivedaysforecast.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ListItem : Serializable {

	 @field:SerializedName("dt")
	 val dt: Int? = null

	 @field:SerializedName("dt_txt")
	 val dtTxt: String? = null

	 @field:SerializedName("weather")
	 val weather: List<WeatherItem?>? = null

	 @field:SerializedName("main")
	 val main: Main? = null

	 @field:SerializedName("clouds")
	 val clouds: Clouds? = null

	 @field:SerializedName("sys")
	 val sys: Sys? = null

	 @field:SerializedName("wind")
	 val wind: Wind? = null

	override fun toString(): String {
		return "ListItem(dt=$dt, dtTxt=$dtTxt, weather=$weather, main=$main, clouds=$clouds, sys=$sys, wind=$wind)"
	}


}