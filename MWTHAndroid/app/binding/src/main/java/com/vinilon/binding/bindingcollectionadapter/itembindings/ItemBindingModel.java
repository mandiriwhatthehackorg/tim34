package com.mandiri.binding.bindingcollectionadapter.itembindings;


import com.mandiri.binding.bindingcollectionadapter.ItemBinding;

/**
 * Implement this interface on yor items to use with {@link OnItemBindModel}.
 */
public interface ItemBindingModel {
    /**
     * Set the binding variable and layout of the given view.
     * <pre>{@code
     * onItemBind.set(BR.item, R.layout.item);
     * }</pre>
     */
    void onItemBind(ItemBinding itemBinding);
}
