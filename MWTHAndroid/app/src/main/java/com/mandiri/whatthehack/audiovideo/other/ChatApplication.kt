package com.mandiri.whatthehack.audiovideo.other

import android.app.Application
import android.content.Intent

import com.mandiri.whatthehack.audiovideo.model.Migration
import com.mandiri.whatthehack.audiovideo.service.SocketIOService
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import com.nostra13.universalimageloader.utils.StorageUtils
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer

import java.io.File

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by andreyyoshuamanik on 9/17/17.
 */

class ChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        bus = MainThreadBus()
        Realm.init(this)

        val realmConfiguration = RealmConfiguration.Builder()
            .schemaVersion(14)
            .migration(Migration())
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)


        val cacheDir = StorageUtils.getCacheDirectory(this)
        val opts = DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build()
        val config = ImageLoaderConfiguration.Builder(this)
            .diskCache(UnlimitedDiskCache(cacheDir))
            .imageDownloader(BaseImageDownloader(this))
            .defaultDisplayImageOptions(opts).build()
        ImageLoader.getInstance().init(config)

    }

    companion object {

        lateinit var bus: MainThreadBus
    }
}
