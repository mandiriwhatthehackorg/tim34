package com.mandiri.agentapp.audiovideo.model

import java.io.Serializable

/**
 * Created by andreyyoshuamanik on 9/13/17.
 */

class CountryCode : Comparable<CountryCode>, Serializable {

    var name: String? = null
    var dial_code: String? = null
    var code: String? = null

    override fun compareTo(countryCode: CountryCode): Int {
        return name!!.compareTo(countryCode.name!!)
    }
}
