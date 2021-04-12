package it.prassel.kotlin.androidfacilitylib.media

class InvalidStatusException : MediaFacilityException {


    constructor(message: String) : super(message) {}

    constructor(message: String, error_code: Int) : super(message, error_code) {}

}
