package com.mandiri.binding

import androidx.databinding.BindingAdapter
import android.graphics.Bitmap
import android.widget.ImageView


/**
 * Created by deryaneveresthamaured on 11/03/18.
 */

@BindingAdapter("idSrc")
fun setImageViewResource(view: ImageView, resId : Int) {
    view.setImageResource(resId)
}


@BindingAdapter("bitmapSrc")
fun setImageViewBitmap(view: ImageView, bitmap: Bitmap?) {
    bitmap ?: return
    view.setImageBitmap(bitmap)
}

@BindingAdapter("assetSrc", "forceDownload", requireAll = false)
fun setImageViewAssets(view: ImageView, imageName: String?, forceDownload: Boolean? = true) {
    imageName?.let {
        if (it != "") {

//            var url = it
//            if (url.contains("~")) {
//                url = url.replace("~", Constants.API_URL)
//            } else {
//                url = "file:///android_asset/$it"
//            }
//
//            // Now load an image for this notification.
//            Picasso.get().load(url)
//                    .into(view)
//
//            PicassoProvider.get() //
//                    .load(PayloadData.URLS[Random().nextInt(PayloadData.URLS.length)]) //
//                    .resizeDimen(R.dimen.notification_icon_width_height,
//                            R.dimen.notification_icon_width_height) //
//                    .into(remoteViews, R.id.photo, NOTIFICATION_ID, notification)
//            var request = Glide.with(view.context)
//                    .load(Uri.parse("file:///android_asset/$it"))
//
//            if (it.contains("~")) {
//                 request = Glide.with(view.context)
//                         .load(Uri.parse(it.replace("~", Constants.API_URL)))
//            }
//            if (forceDownload == true) {
//                val requestOptions = RequestOptions()
//
//                request.apply(requestOptions.skipMemoryCache(true))
//                        .apply(requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE))
//                        .into(view)
//            } else {
//                request.into(view)
//            }
        }
    }
}

@BindingAdapter("urlSrc", "forceDownload", requireAll = false)
fun setImageViewFromUrl(view: ImageView, imageName: String?, forceDownload: Boolean? = true) {
    imageName?.let {
        if (it != "") {

//            var url = it
//            if (url.contains("~")) {
//                url = url.replace("~", Constants.API_URL)
//            }
//
//
//            // Now load an image for this notification.
//            Picasso.get().load(url)
////                    .placeholder(R.drawable.logo)
//                    .fit()
//                    .into(view)

        } else {
//            Picasso.get().load(R.drawable.logo).fit().into(view)
        }
    }
}
