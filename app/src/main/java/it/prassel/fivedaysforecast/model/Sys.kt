package it.prassel.fivedaysforecast.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

 class Sys : Serializable {

	@field:SerializedName("pod")
	val pod: String? = null
}