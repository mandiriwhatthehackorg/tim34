package com.mandiri.domain.repository

import androidx.lifecycle.LiveData
import com.mandiri.domain.API
import com.mandiri.domain.models.CreateSessionRes
import com.mandiri.domain.models.GetKYCDataReq
import com.mandiri.domain.models.GetKYCDataRes
import com.mandiri.domain.models.SubmitKYCReq
import com.mandiri.domain.network.KYCService
import com.mandiri.domain.usecase.KYCUseCase
import com.mandiri.domain.utils.ApiResponse

class KYCRepository: KYCUseCase {

    val service = API.retrofitOnboarding.create(KYCService::class.java)

    override fun doKYC(data: SubmitKYCReq): LiveData<ApiResponse<CreateSessionRes>> {
        return service.submitKyCResult(data)
    }

    override fun getKYCData(data: GetKYCDataReq): LiveData<ApiResponse<GetKYCDataRes>> {
        return service.getKYCData(data)
    }
}