package it.prassel.kotlin.androidfacilitylib.media

import it.prassel.fivedaysforecast.util.Util
import java.util.Calendar


class BadPermissionException : Exception {

    var missingPermissions: Array<String>? = null
        internal set

    override var message: String? = null
    var userMessage: String? = null
        internal set
    internal var user_name: String? = null
    var errorCode = 0
        internal set

    constructor(message: String, permissions: Array<String>) {
        this.userMessage = message
        this.message = "[" + Util.calendar2String(Calendar.getInstance()) + "] " + message
        missingPermissions = permissions
    }

    constructor(message: String, error_code: Int, permissions: Array<String>) {
        this.userMessage = message
        this.message = "[" + Util.calendar2String(Calendar.getInstance()) + "] " + message
        this.errorCode = error_code
        missingPermissions = permissions
    }


    companion object {

        /**
         * Comment for `serialVersionUID`
         */
        private val serialVersionUID = 1L
    }
}
