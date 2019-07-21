package com.mandiri.whatthehack.onboarding.pages

import android.graphics.Bitmap
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mandiri.binding.bindingcollectionadapter.ItemBinding
import com.mandiri.domain.models.*
import com.mandiri.domain.repository.OnBoardingRepository
import io.reactivex.internal.operators.observable.ObservableFlattenIterable

class OnboardingViewModel: ViewModel() {

    val onBoardingUseCase = OnBoardingRepository()

    // First Step (Create Session)
    val email = ObservableField<String>()
    val nik = ObservableField<String>()
    val phone = ObservableField<String>()
    val ttl = ObservableField<String>()

    // Second Step (OTP)
    val otp = ObservableField<String>()

    // Third Step (Input Data)
    val motherName = ObservableField<String>()

    val productTypeOptions = ObservableField<List<Product>>()
    val cardListOptions = ObservableField<List<Card>>()
    val branchOptions = ObservableField<List<Branch>>()
    val customerData = ObservableField<CustomerData>()


    val selectedBranch = ObservableField<Branch>()
    val selectedProduct = ObservableField<Product>()
    val selectedCard = ObservableField<Card>()
    val selectedBranchStr = ObservableField<String>()
    val selectedProductStr = ObservableField<String>()
    val selectedCardStr = ObservableField<String>()

    val ktpImage = ObservableField<Bitmap>()
    val selfieImage = ObservableField<Bitmap>()
    val signatureImage = ObservableField<Bitmap>()

    init {
        branchOptions.set(listOf(Branch("Hahahah", "Hihihi", "Hohooho"), Branch("Hahahah", "Hihihi", "Hohooho"),
            Branch("Hahahah", "Hihihi", "Hohooho")
        ))

        selectedBranch.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                selectedBranchStr.set(selectedBranch.get()?.branchName)
            }

        })
        selectedCard.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                selectedCardStr.set(selectedCard.get()?.cardName)
            }

        })
        selectedProduct.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                selectedProductStr.set(selectedProduct.get()?.productName)
            }

        })
    }

    fun createSession(): LiveData<CreateSessionRes> {
        val data = CreateSessionReq(email.get() ?: "", nik.get() ?: "", phone.get() ?: "", ttl.get() ?: "")
        return onBoardingUseCase.createSession(data)
    }

    fun validateOtp(): LiveData<ValidateOTPRes> {
        return onBoardingUseCase.validateOtp(otp.get() ?: "")
    }

    fun resendOtp(): LiveData<CreateSessionRes> {
        return onBoardingUseCase.resendOtp(nik.get() ?: "12121212")
    }

    fun submitData(): LiveData<CreateSessionRes> {
        val data = SubmitDataReq(
            selectedBranch.get()?.branchCode ?: "",
            selectedCard.get()?.cardCode ?: "",
            motherName.get() ?: "",
            selectedProduct.get()?.productCode ?: "")
        return onBoardingUseCase.submitData(data)
    }

    fun submitKtp(): LiveData<CreateSessionRes> {
        return onBoardingUseCase.submitImageKtp(ktpImage.get()!!)
    }

    fun submitSelfie(): LiveData<CreateSessionRes> {
        return onBoardingUseCase.submitImageSelfie(selfieImage.get()!!)
    }

    fun submitSignature(): LiveData<SubmitSignatureRes> {
        return onBoardingUseCase.submitImageSignature(signatureImage.get()!!)
    }
    
}