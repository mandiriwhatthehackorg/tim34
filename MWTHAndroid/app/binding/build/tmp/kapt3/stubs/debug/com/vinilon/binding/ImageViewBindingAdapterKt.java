package com.mandiri.binding;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 15}, bv = {1, 0, 3}, k = 2, d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\u001a+\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007\u00a2\u0006\u0002\u0010\b\u001a\u001a\u0010\t\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0007\u001a+\u0010\f\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007\u00a2\u0006\u0002\u0010\b\u001a\u0018\u0010\r\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000fH\u0007\u00a8\u0006\u0010"}, d2 = {"setImageViewAssets", "", "view", "Landroid/widget/ImageView;", "imageName", "", "forceDownload", "", "(Landroid/widget/ImageView;Ljava/lang/String;Ljava/lang/Boolean;)V", "setImageViewBitmap", "bitmap", "Landroid/graphics/Bitmap;", "setImageViewFromUrl", "setImageViewResource", "resId", "", "binding_debug"})
public final class ImageViewBindingAdapterKt {
    
    /**
     * Created by deryaneveresthamaured on 11/03/18.
     */
    @androidx.databinding.BindingAdapter(value = {"idSrc"})
    public static final void setImageViewResource(@org.jetbrains.annotations.NotNull()
    android.widget.ImageView view, int resId) {
    }
    
    @androidx.databinding.BindingAdapter(value = {"bitmapSrc"})
    public static final void setImageViewBitmap(@org.jetbrains.annotations.NotNull()
    android.widget.ImageView view, @org.jetbrains.annotations.Nullable()
    android.graphics.Bitmap bitmap) {
    }
    
    @androidx.databinding.BindingAdapter(requireAll = false, value = {"assetSrc", "forceDownload"})
    public static final void setImageViewAssets(@org.jetbrains.annotations.NotNull()
    android.widget.ImageView view, @org.jetbrains.annotations.Nullable()
    java.lang.String imageName, @org.jetbrains.annotations.Nullable()
    java.lang.Boolean forceDownload) {
    }
    
    @androidx.databinding.BindingAdapter(requireAll = false, value = {"urlSrc", "forceDownload"})
    public static final void setImageViewFromUrl(@org.jetbrains.annotations.NotNull()
    android.widget.ImageView view, @org.jetbrains.annotations.Nullable()
    java.lang.String imageName, @org.jetbrains.annotations.Nullable()
    java.lang.Boolean forceDownload) {
    }
}