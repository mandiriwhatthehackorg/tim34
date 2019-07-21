package com.mandiri.domain.usecase

import androidx.lifecycle.LiveData
import com.mandiri.domain.API
import com.mandiri.domain.models.JWTRes

interface JWTUseCase {

    fun getToken(appId: String = API.appId): LiveData<JWTRes>
}