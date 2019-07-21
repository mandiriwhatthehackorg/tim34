package com.mandiri.whatthehack.onboarding.pages


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.mandiri.domain.Constants
import com.mandiri.domain.models.CreateSessionRes

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.databinding.FragmentInputDataBinding
import com.mandiri.whatthehack.onboarding.domain.SharedPrefs
import kotlinx.android.synthetic.main.fragment_input_data.*

class InputDataFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(activity!!).get(OnboardingViewModel::class.java) }
    lateinit var dataBinding: FragmentInputDataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_input_data, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.viewModel = viewModel

        viewModel.branchOptions.get()?.let {
            val branchSpinnerAdapter = ArrayAdapter<String>(
                activity!!, android.R.layout.simple_spinner_item,
                viewModel.branchOptions.get()!!.map { it.toString() }
            ) //selected item will look like a spinner set from XML
            branchSpinnerAdapter.setDropDownViewResource(
                android.R.layout
                    .simple_spinner_dropdown_item
            )
            branchSpinner.adapter = branchSpinnerAdapter
        }

        viewModel.productTypeOptions.get()?.let {
            val productSpinnerAdapter = ArrayAdapter<String>(
                activity!!, android.R.layout.simple_spinner_item,
                viewModel.productTypeOptions.get()!!.map { it.toString() }
            ) //selected item will look like a spinner set from XML
            productSpinnerAdapter.setDropDownViewResource(
                android.R.layout
                    .simple_spinner_dropdown_item
            )
            productSpinner.adapter = productSpinnerAdapter
        }

        viewModel.cardListOptions.get()?.let {

            val cardSpinnerAdapter = ArrayAdapter<String>(
                activity!!, android.R.layout.simple_spinner_item,
                viewModel.cardListOptions.get()!!.map { it.toString() }
            ) //selected item will look like a spinner set from XML
            cardSpinnerAdapter.setDropDownViewResource(
                android.R.layout
                    .simple_spinner_dropdown_item
            )
            cardSpinner.adapter = cardSpinnerAdapter
        }

        cardSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.selectedCard.set(viewModel.cardListOptions.get()?.get(p2))
            }
        }
        branchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.selectedBranch.set(viewModel.branchOptions.get()?.get(p2))
            }
        }
        productSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.selectedProduct.set(viewModel.productTypeOptions.get()?.get(p2))
            }
        }

        submitButton.setOnClickListener {
            val submitData = viewModel.submitData()
            submitData.observe(this, object : Observer<CreateSessionRes> {
                override fun onChanged(t: CreateSessionRes?) {
                    t?.let {
                        if (!it.success || it.data == null) {
                            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                        } else {
                            SharedPrefs.set(activity!!, Constants.KEY_TOKEN.value!!, it.data?.token)
                            SharedPrefs.set(activity!!, Constants.KEY_STEP.value!!, Constants.VALUE_STEP_KTP.value)

                            findNavController().navigate(R.id.goToKtp)
                        }
                    }
                    submitData.removeObserver(this)
                }

            })
        }
    }

}
