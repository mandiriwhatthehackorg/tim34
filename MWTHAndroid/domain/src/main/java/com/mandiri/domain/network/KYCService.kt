package com.mandiri.domain.network

import androidx.lifecycle.LiveData
import com.mandiri.domain.models.CreateSessionRes
import com.mandiri.domain.models.GetKYCDataReq
import com.mandiri.domain.models.GetKYCDataRes
import com.mandiri.domain.models.SubmitKYCReq
import com.mandiri.domain.utils.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface KYCService {

    @POST("getKYCData")
    fun getKYCData(@Body data: GetKYCDataReq): LiveData<ApiResponse<GetKYCDataRes>>

    @POST("SubmitKYCResult")
    fun submitKyCResult(@Body data: SubmitKYCReq): LiveData<ApiResponse<CreateSessionRes>>
}