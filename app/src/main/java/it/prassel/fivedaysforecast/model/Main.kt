package it.prassel.fivedaysforecast.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Main : Serializable{

	@field:SerializedName("temp")
	var temp: Double? = null

	@field:SerializedName("temp_min")
	var tempMin: Double? = null

	@field:SerializedName("grnd_level")
	var grndLevel: Int? = null

	@field:SerializedName("temp_kf")
	var tempKf: Double? = null

	@field:SerializedName("humidity")
	var humidity: Int? = null

	@field:SerializedName("pressure")
	var pressure: Int? = null

	@field:SerializedName("sea_level")
	var seaLevel: Int? = null

	@field:SerializedName("feels_like")
	var feelsLike: Double? = null

	@field:SerializedName("temp_max")
	var tempMax: Double? = null

	override fun toString(): String {
		return "Main(temp=$temp, tempMin=$tempMin, grndLevel=$grndLevel, tempKf=$tempKf, humidity=$humidity, pressure=$pressure, seaLevel=$seaLevel, feelsLike=$feelsLike, tempMax=$tempMax)"
	}


}