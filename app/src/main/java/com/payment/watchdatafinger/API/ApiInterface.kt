package com.payment.indistart.API


import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


internal interface APIInterface {


    @POST("api/v1/aepsTransaction")
    fun doCreateUserWithField(
        @Body data: RequestBody
    ): Call<ResponseBody>
}
