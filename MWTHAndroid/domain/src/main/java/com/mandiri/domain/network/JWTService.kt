package com.mandiri.domain.network

import androidx.lifecycle.LiveData
import com.mandiri.domain.models.JWTRes
import com.mandiri.domain.utils.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JWTService {

    @GET("getJsonWebToken")
    fun getToken(@Query("app_id") appId: String): LiveData<ApiResponse<JWTRes>>
}