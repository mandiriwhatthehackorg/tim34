package com.mandiri.binding;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 15}, bv = {1, 0, 3}, k = 2, d1 = {"\u0000\u001c\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0012\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\u001a\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007\u001a\u001a\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u00032\b\u0010\n\u001a\u0004\u0018\u00010\u0001H\u0007\u00a8\u0006\u000b"}, d2 = {"getRealValue", "", "spinner", "Landroid/widget/RatingBar;", "setListener", "", "listener", "Landroidx/databinding/InverseBindingListener;", "setRealValue", "view", "value", "binding_debug"})
public final class RatingBarBindingAdapterKt {
    
    @androidx.databinding.BindingAdapter(value = {"realValueAttrChanged"})
    public static final void setListener(@org.jetbrains.annotations.NotNull()
    android.widget.RatingBar spinner, @org.jetbrains.annotations.Nullable()
    androidx.databinding.InverseBindingListener listener) {
    }
    
    @androidx.databinding.BindingAdapter(value = {"realValue"})
    public static final void setRealValue(@org.jetbrains.annotations.NotNull()
    android.widget.RatingBar view, @org.jetbrains.annotations.Nullable()
    java.lang.Object value) {
    }
    
    @org.jetbrains.annotations.Nullable()
    @androidx.databinding.InverseBindingAdapter(attribute = "realValue")
    public static final java.lang.Object getRealValue(@org.jetbrains.annotations.NotNull()
    android.widget.RatingBar spinner) {
        return null;
    }
}