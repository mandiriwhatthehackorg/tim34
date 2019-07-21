package com.mandiri.domain.network

import androidx.lifecycle.LiveData
import com.mandiri.domain.models.*
import com.mandiri.domain.utils.ApiResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface OnBoardingService {

    @POST("initiate/createSession")
    fun createSession(@Body data: CreateSessionReq): LiveData<ApiResponse<CreateSessionRes>>

    @POST("initiate/resendOTP")
    fun resendOtp(@Body data: ResendOTPReq): LiveData<ApiResponse<CreateSessionRes>>

    @POST("initiate/validateOTP")
    fun validateOtp(@Body data: ValidateOTPReq): LiveData<ApiResponse<ValidateOTPRes>>

    @POST("submitData")
    fun submitData(@Body data: SubmitDataReq): LiveData<ApiResponse<CreateSessionRes>>

    @Multipart
    @POST("submitImageKTP")
    fun submitImageKtp(@Part files: Array<MultipartBody.Part>): LiveData<ApiResponse<CreateSessionRes>>

    @Multipart
    @POST("submitImageSelfie")
    fun submitImageSelfie(@Part files: Array<MultipartBody.Part>): LiveData<ApiResponse<CreateSessionRes>>

    @Multipart
    @POST("submitImageSignature")
    fun submitImageSignature(@Part files: Array<MultipartBody.Part>): LiveData<ApiResponse<SubmitSignatureRes>>

}