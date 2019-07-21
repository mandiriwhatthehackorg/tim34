package com.mandiri.binding;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 15}, bv = {1, 0, 3}, k = 2, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\u001a\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007\u001a\u0018\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000bH\u0007\u001a\u001a\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u00032\b\u0010\f\u001a\u0004\u0018\u00010\u0001H\u0007\u00a8\u0006\r"}, d2 = {"getRealValue", "", "TextView", "Landroid/widget/TextView;", "setListener", "", "listener", "Landroidx/databinding/InverseBindingListener;", "setRealValue", "view", "movementMethod", "Landroid/text/method/LinkMovementMethod;", "value", "binding_debug"})
public final class TextViewBindingAdapterKt {
    
    @androidx.databinding.BindingAdapter(value = {"realValueAttrChanged"})
    public static final void setListener(@org.jetbrains.annotations.NotNull()
    android.widget.TextView TextView, @org.jetbrains.annotations.Nullable()
    androidx.databinding.InverseBindingListener listener) {
    }
    
    @androidx.databinding.BindingAdapter(value = {"realValue"})
    public static final void setRealValue(@org.jetbrains.annotations.NotNull()
    android.widget.TextView view, @org.jetbrains.annotations.Nullable()
    java.lang.String value) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @androidx.databinding.InverseBindingAdapter(attribute = "realValue")
    public static final java.lang.String getRealValue(@org.jetbrains.annotations.NotNull()
    android.widget.TextView TextView) {
        return null;
    }
    
    @androidx.databinding.BindingAdapter(value = {"movementMethod"})
    public static final void setRealValue(@org.jetbrains.annotations.NotNull()
    android.widget.TextView view, @org.jetbrains.annotations.NotNull()
    android.text.method.LinkMovementMethod movementMethod) {
    }
}