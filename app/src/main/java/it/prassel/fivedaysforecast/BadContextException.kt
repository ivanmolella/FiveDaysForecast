package it.prassel.fivedaysforecast


import it.prassel.fivedaysforecast.util.Util
import java.util.Calendar



/**
 * Created by ivan on 14/10/16.
 */

class BadContextException : Exception {

    override var message: String? = null
    var userMessage: String? = null
        internal set
    internal var user_name: String? = null
    var errorCode = 0
        internal set

    constructor(message: String?, user_name: String?) {
        this.userMessage = message
        this.message = "[" + Util.calendar2String(Calendar.getInstance()) + "] " + message
    }

    constructor(message: String?, user_name: String?, error_code: Int) {
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
