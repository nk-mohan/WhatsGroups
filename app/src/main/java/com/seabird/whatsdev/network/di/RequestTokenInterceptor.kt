package com.seabird.whatsdev.network.di

import android.util.Log
import com.seabird.whatsdev.utils.AppConstants
import com.seabird.whatsdev.utils.SharedPreferenceManager
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RequestTokenInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        return if (AppConstants.BASE_URL.contains(request.url.host)) {
            val newRequest: Request
            val token = SharedPreferenceManager.getStringValue(AppConstants.ACCESS_TOKEN)?.trim { it <= ' ' } ?: ""
            newRequest = request.newBuilder().header("Authorization", "Bearer $token").method(request.method, request.body).build()
            Log.d("RequestTokenInterceptor", newRequest.toString())
            try {
                chain.proceed(newRequest)
            } catch (e: Exception) {
                Log.e("RequestTokenInterceptor", e.message.toString())
                e.printStackTrace()
                val msg = when (e) {
                    is SocketTimeoutException -> "Timeout - Please check your internet connection"
                    is UnknownHostException -> "Unable to make a connection. Please check your internet"
                    is ConnectionShutdownException -> "Connection shutdown. Please check your internet"
                    is IOException -> "Server is unreachable, please try again later."
                    is IllegalStateException -> "${e.message}"
                    else -> "${e.message}"
                }
                Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(999)
                    .message(msg).body("{${e}}".toResponseBody(null)).build()
            }
        } else {
            Log.d("RequestTokenInterceptor", "skipped adding auth headers")
            chain.proceed(request)
        }
    }
}