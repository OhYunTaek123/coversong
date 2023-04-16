package com.example.coversong.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MusicInterface {

    @POST("")
    fun getMusicInterface(@Body musicInterface: MusicInterface ): Call<String>
}