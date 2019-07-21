package com.mandiri.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mandiri.domain.API
import com.mandiri.domain.models.JWTRes
import com.mandiri.domain.network.JWTService
import com.mandiri.domain.usecase.JWTUseCase

class JWTRepository: JWTUseCase {

    private val service = API.retrofitJWT.create(JWTService::class.java)

    override fun getToken(appId: String): LiveData<JWTRes> {
        return Transformations.map(service.getToken(appId)) {
            it.body
        }
    }
}