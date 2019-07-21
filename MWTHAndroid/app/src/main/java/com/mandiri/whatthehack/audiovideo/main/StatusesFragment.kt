package com.mandiri.whatthehack.audiovideo.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mandiri.whatthehack.R


/**
 * A simple [Fragment] subclass.
 */
class StatusesFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_statuses, container, false)
    }

}// Required empty public constructor
