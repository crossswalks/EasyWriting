package com.niki.easy

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * normal way to make requests
 */
open class WBaseRepository {
    protected val TAG: String = this::class.java.name

    protected fun okHttpClientBuilder(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    /**
     *  when call enqueue(), Retrofit will make a request in background
     */
    protected fun <T> enqueueCall(call: Call<T>, callback: (T?, Int?, String?) -> Unit) =
        call.enqueue(object : Callback<T> {
            /**
             * this is the implements of Callback
             * i love it
             */
            override fun onResponse(call: Call<T>, response: Response<T>) {
                Log.d(TAG, "onResponse: ${response.message()}")
                if (response.isSuccessful) {
                    callback(response.body(), response.code(), response.message())
                } else {
                    callback(null, response.code(), response.message())
                }
            }

            /**
             * maybe you got better ways
             */
            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                callback(null, 999, "onFailure: ${t.message}")
            }
        })
}