package it.prassel.fivedaysforecast.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Wind : Serializable {

	@field:SerializedName("deg")
	val deg: Int? = null

	@field:SerializedName("speed")
    var speed: Double? = null

	override fun toString(): String {
		return "Wind(deg=$deg, speed=$speed)"
	}


}