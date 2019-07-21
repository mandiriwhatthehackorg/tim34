package com.mandiri.domain.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mandiri.domain.API
import com.mandiri.domain.Constants
import com.mandiri.domain.models.*
import com.mandiri.domain.network.OnBoardingService
import com.mandiri.domain.usecase.OnBoardingUseCase
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class OnBoardingRepository: OnBoardingUseCase {

    private val service = API.retrofitOnboarding.create(OnBoardingService::class.java)

    override fun createSession(data: CreateSessionReq): LiveData<CreateSessionRes> {
        return Transformations.map(service.createSession(data)) {
            Constants.Token.value = it.body?.data?.token
            it.body
        }
    }

    override fun resendOtp(nik: String): LiveData<CreateSessionRes> {
        val data = ResendOTPReq(nik)
        return Transformations.map(service.resendOtp(data)) {
            Constants.Token.value = it.body?.data?.token
            it.body
        }
    }

    override fun validateOtp(otp: String): LiveData<ValidateOTPRes> {
        return Transformations.map(service.validateOtp(ValidateOTPReq(otp))) {
            Constants.Token.value = it.body?.data?.token
            it.body
        }
    }

    override fun submitData(data: SubmitDataReq): LiveData<CreateSessionRes> {
        return Transformations.map(service.submitData(data)) {
            Constants.Token.value = it.body?.data?.token
            it.body
        }
    }

    override fun submitImageKtp(bitmap: Bitmap): LiveData<CreateSessionRes> {
        val files = arrayOf(bitmapToPart("file", bitmap))
        return Transformations.map(service.submitImageKtp(files)) {
            Constants.Token.value = it.body?.data?.token
            it.body
        }
    }

    override fun submitImageSelfie(bitmap: Bitmap): LiveData<CreateSessionRes> {
        val files = arrayOf(bitmapToPart("file", bitmap))
        return Transformations.map(service.submitImageSelfie(files)) {
            Constants.Token.value = it.body?.data?.token
            it.body
        }
    }

    override fun submitImageSignature(bitmap: Bitmap): LiveData<SubmitSignatureRes> {
        val files = arrayOf(bitmapToPart("file", bitmap))
        return Transformations.map(service.submitImageSignature(files)) {
            Constants.Token.value = it.body?.data?.token
            it.body
        }
    }

    private fun bitmapToPart(name: String, bitmap: Bitmap): MultipartBody.Part {

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        val requestBody = RequestBody
            .create(MediaType.parse("application/octet-stream"), byteArray)
        val part = MultipartBody.Part.createFormData(name, "photo.png", requestBody)
        return part
    }
}