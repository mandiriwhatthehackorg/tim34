package com.mandiri.domain.usecase

import androidx.lifecycle.LiveData
import com.mandiri.domain.models.CreateSessionRes
import com.mandiri.domain.models.GetKYCDataReq
import com.mandiri.domain.models.GetKYCDataRes
import com.mandiri.domain.models.SubmitKYCReq
import com.mandiri.domain.utils.ApiResponse

interface KYCUseCase {

    fun getKYCData(data: GetKYCDataReq): LiveData<ApiResponse<GetKYCDataRes>>
    fun doKYC(data: SubmitKYCReq): LiveData<ApiResponse<CreateSessionRes>>
}