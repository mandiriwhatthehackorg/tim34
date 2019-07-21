package com.mandiri.agentapp.audiovideo.main

import android.content.Intent
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.widget.Toast

import com.mandiri.agentapp.audiovideo.SettingsActivity
import com.mandiri.agentapp.audiovideo.chat.ChatActivity
import com.mandiri.agentapp.R
import com.mandiri.agentapp.audiovideo.addgroup.SelectParticipantActivity
import com.mandiri.agentapp.audiovideo.chat.SelectContactActivity
import com.mandiri.agentapp.audiovideo.model.User
import com.mandiri.agentapp.audiovideo.other.ChatApplication
import com.mandiri.agentapp.audiovideo.service.SocketIOService
import com.squareup.otto.Subscribe

import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), UserListFragment.OnUserListListener, ChatListFragment.OnChatListListener {

    override var realm: Realm = Realm.getDefaultInstance()
    internal var myself: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(toolbar)

        // Start Socket service
        // The service will be connecting the socket
        startService(Intent(this, SocketIOService::class.java))

        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                when (position) {
                    0 -> return ChatListFragment()
                    1 -> return StatusesFragment()
                    2 -> return CallListFragment()
                }
                return ChatListFragment()
            }

            override fun getCount(): Int {
                return 3
            }

            override fun getPageTitle(position: Int): CharSequence {
                when (position) {
                    0 -> return "Chats"
                    1 -> return "Status"
                    2 -> return "Calls"
                }
                return "Chats"
            }
        }

        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                animateFab(0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        firstFAB.setOnClickListener { firstFabClicked() }
    }

    override fun onResume() {
        super.onResume()
        ChatApplication.bus.register(this)
    }

    override fun onPause() {
        super.onPause()
        ChatApplication.bus.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
        stopService(Intent(this, SocketIOService::class.java))
    }

    // Called when any event with string parameter posted by socket service
    @Subscribe
    fun receiveEventFromSocket(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
            }
            R.id.newGroup -> startActivity(Intent(this, SelectParticipantActivity::class.java))
            R.id.newBroadcast -> {
            }
            R.id.chatWeb -> {
            }
            R.id.starredMessage -> {
            }
            R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }


    internal fun firstFabClicked() {

        val currentFragment = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + viewPager!!.currentItem)
        if (currentFragment is ChatListFragment) {
            startActivity(Intent(this, SelectContactActivity::class.java))
        }
    }


    protected fun animateFab(position: Int) {
        firstFAB!!.clearAnimation()
        // Scale down animation
        val shrink = ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        shrink.duration = 150     // animation duration in milliseconds
        shrink.interpolator = DecelerateInterpolator()
        shrink.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                // Change FAB color and icon
                //                firstFAB.setBackgroundTintList(getResources().getColorStateList(colorIntArray[position]));
                //                firstFAB.setImageDrawable(getResources().getDrawable(iconIntArray[position], null));

                // Scale up animation
                val expand = ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                expand.duration = 100     // animation duration in milliseconds
                expand.interpolator = AccelerateInterpolator()
                firstFAB!!.startAnimation(expand)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        firstFAB!!.startAnimation(shrink)
    }

    override fun onClickUser(userId: String?) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(ChatActivity.USER_ID, userId)

        startActivity(intent)
    }

    override fun onClickChat(chatId: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(ChatActivity.CHAT_ID, chatId)

        startActivity(intent)
    }
}
