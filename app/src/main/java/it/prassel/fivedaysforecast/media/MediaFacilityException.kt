package it.prassel.kotlin.androidfacilitylib.media

import it.prassel.fivedaysforecast.util.Util
import java.util.Calendar


open class MediaFacilityException : Exception {

    override var message: String? = null
    var userMessage: String? = null
        internal set
    internal var user_name: String? = null
    var errorCode = 0
        internal set

    constructor(message: String) {
        this.userMessage = message
        this.message = "[" + Util.calendar2String(Calendar.getInstance()) + "] " + message
    }

    constructor(message: String, error_code: Int) {
        this.userMessage = message
        this.message = "[" + Util.calendar2String(Calendar.getInstance()) + "] " + message
        this.errorCode = error_code
    }

    companion object {

        /**
         * Comment for `serialVersionUID`
         */
        private val serialVersionUID = 1L
    }
}
