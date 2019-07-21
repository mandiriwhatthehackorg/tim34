package com.mandiri.agentapp.audiovideo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.mandiri.agentapp.R

import com.mandiri.agentapp.audiovideo.other.RecyclerItemClickListener
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {

    private val menus = arrayOf("Account", "Chats", "Notifications", "Data Usage", "Contacts", "Help")
    private val menuIcons = intArrayOf(
        R.drawable.ic_key_color_primary,
        R.drawable.ic_chat_color_primary,
        R.drawable.ic_notifications_color_primary,
        R.drawable.ic_data_usage_color_primary,
        R.drawable.ic_contacts_color_primary,
        R.drawable.ic_help_color_primary
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        recyclerView!!.adapter = SettingsAdapter()
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {

                    }

                    override fun onLongItemClick(view: View?, position: Int) {

                    }
                })
        )

        userImageButton.setOnClickListener { changeUserImage() }
    }

    internal fun changeUserImage() {

    }

    private inner class SettingsAdapter : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.settings_menu_row, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.menuTextView.text = menus[position]
            holder.iconImageView.setImageResource(menuIcons[position])
        }

        override fun getItemCount(): Int {
            return menus.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var iconImageView: ImageView
            var menuTextView: TextView

            init {

                iconImageView = itemView.findViewById(R.id.iconImageView)
                menuTextView = itemView.findViewById(R.id.menuTextView)
            }
        }
    }
}
