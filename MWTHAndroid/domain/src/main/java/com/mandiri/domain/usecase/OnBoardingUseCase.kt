package com.mandiri.domain.usecase

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.mandiri.domain.models.*
import okhttp3.MultipartBody

interface OnBoardingUseCase {

    fun createSession(data: CreateSessionReq): LiveData<CreateSessionRes>
    fun resendOtp(nik: String): LiveData<CreateSessionRes>
    fun validateOtp(otp: String): LiveData<ValidateOTPRes>
    fun submitData(data: SubmitDataReq): LiveData<CreateSessionRes>
    fun submitImageKtp(bitmap: Bitmap): LiveData<CreateSessionRes>
    fun submitImageSelfie(bitmap: Bitmap): LiveData<CreateSessionRes>
    fun submitImageSignature(bitmap: Bitmap): LiveData<SubmitSignatureRes>
}