package com.mandiri.whatthehack.audiovideo.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

import com.mandiri.whatthehack.R
import com.mandiri.whatthehack.audiovideo.model.CountryCode
import com.mandiri.whatthehack.audiovideo.other.RecyclerItemClickListener
import com.google.gson.Gson

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.Arrays

import kotlinx.android.synthetic.main.activity_country_code.*

class CountryCodeActivity : AppCompatActivity() {

    internal var codes: Array<CountryCode>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_code)

        var bufferedReader: BufferedReader? = null
        try {

            val json = assets.open("country_code.json")
            bufferedReader = BufferedReader(InputStreamReader(json, "UTF-8"))
            var str: String = bufferedReader.readText();

            codes = Gson().fromJson(str, Array<CountryCode>::class.java)
            Arrays.sort(codes!!)
            bufferedReader.close()

        } catch (e: IOException) {
            //log the exception
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                } catch (e: IOException) {
                    //log the exception
                }

            }
            if (codes != null) {
                val name = ArrayList<String>()
                for (countryCode in codes!!) {
                    name.add(countryCode.name ?: "")
                }
                recyclerView.adapter = CountryCodeAdapter(ArrayList(Arrays.asList(*codes!!)))
                recyclerView!!.layoutManager = LinearLayoutManager(this)
                recyclerView!!.addOnItemTouchListener(RecyclerItemClickListener(this, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val intent = Intent()
                        intent.putExtra(COUNTRY_CODE, (recyclerView!!.adapter as CountryCodeAdapter).codes[position])
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                    override fun onLongItemClick(view: View?, position: Int) {

                    }
                }))

                searchEditText!!.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                    }

                    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

                    override fun afterTextChanged(editable: Editable) {

                        val inputted = editable.toString()
                        if (inputted == "") {
                            recyclerView!!.adapter = CountryCodeAdapter(ArrayList(Arrays.asList(*codes!!)))
                            return
                        }
                        val countryCodes = ArrayList<CountryCode>()
                        for (code in codes!!) {
                            if (code.name?.toLowerCase()?.contains(inputted.toLowerCase()) == true) {
                                countryCodes.add(code)
                            }
                        }
                        recyclerView!!.adapter = CountryCodeAdapter(countryCodes)
                    }
                })
            }
        }

        backButton.setOnClickListener { back() }

    }


    internal fun back() {
        finish()
    }

    private inner class CountryCodeAdapter internal constructor(countryCodes: ArrayList<CountryCode>) : RecyclerView.Adapter<CountryCodeAdapter.ViewHolder>() {

        internal var codes = ArrayList<CountryCode>()

        init {
            this.codes = countryCodes
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.country_code_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.countryTextView.text = codes[position].name
            holder.codeTextView.text = codes[position].dial_code
        }

        override fun getItemCount(): Int {
            return codes.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var countryTextView: TextView
            var codeTextView: TextView

            init {
                countryTextView = itemView.findViewById(R.id.countryTextView)
                codeTextView = itemView.findViewById(R.id.codeTextView)
            }
        }
    }

    companion object {

        val COUNTRY_CODE = "countryCode"
    }
}
