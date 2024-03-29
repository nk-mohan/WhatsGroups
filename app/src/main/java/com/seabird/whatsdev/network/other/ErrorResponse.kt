package com.seabird.whatsdev.network.other

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class ErrorResponse {
    @SerializedName("error")
    @Expose
    private var error: String? = null

    fun getError(): String? {
        return error
    }

    fun setError(error: String?) {
        this.error = error
    }
}