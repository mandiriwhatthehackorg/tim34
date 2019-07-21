package com.mandiri.whatthehack.audiovideo.other

import java.util.ArrayList

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class FrameLayoutFixed : FrameLayout {
    private val mMatchParentChildren = ArrayList<View>(1)

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    fun getMeasuredStateFixed(view: View): Int {
        return view.measuredWidth and -0x1000000 or (view.measuredHeight shr 16 and (-0x1000000 shr 16))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        try {
            var count = childCount

            val measureMatchParentChildren =
                View.MeasureSpec.getMode(widthMeasureSpec) != View.MeasureSpec.EXACTLY || View.MeasureSpec.getMode(
                    heightMeasureSpec
                ) != View.MeasureSpec.EXACTLY
            mMatchParentChildren.clear()

            var maxHeight = 0
            var maxWidth = 0
            var childState = 0

            for (i in 0 until count) {
                val child = getChildAt(i)
                if (child.visibility != View.GONE) {
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                    val lp = child.layoutParams as FrameLayout.LayoutParams
                    maxWidth = Math.max(
                        maxWidth,
                        child.measuredWidth + lp.leftMargin + lp.rightMargin
                    )
                    maxHeight = Math.max(
                        maxHeight,
                        child.measuredHeight + lp.topMargin + lp.bottomMargin
                    )
                    childState = childState or getMeasuredStateFixed(child)
                    if (measureMatchParentChildren) {
                        if (lp.width == FrameLayout.LayoutParams.MATCH_PARENT || lp.height == FrameLayout.LayoutParams.MATCH_PARENT) {
                            mMatchParentChildren.add(child)
                        }
                    }
                }
            }

            // Account for padding too
            maxWidth += paddingLeft + paddingRight
            maxHeight += paddingTop + paddingBottom

            // Check against our minimum height and width
            maxHeight = Math.max(maxHeight, suggestedMinimumHeight)
            maxWidth = Math.max(maxWidth, suggestedMinimumWidth)

            // Check against our foreground's minimum height and width
            val drawable = foreground
            if (drawable != null) {
                maxHeight = Math.max(maxHeight, drawable.minimumHeight)
                maxWidth = Math.max(maxWidth, drawable.minimumWidth)
            }

            setMeasuredDimension(
                resolveSizeAndStateFixed(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndStateFixed(maxHeight, heightMeasureSpec, childState shl View.MEASURED_HEIGHT_STATE_SHIFT)
            )

            count = mMatchParentChildren.size
            if (count > 1) {
                for (i in 0 until count) {
                    val child = mMatchParentChildren[i]

                    val lp = child.layoutParams as ViewGroup.MarginLayoutParams
                    val childWidthMeasureSpec: Int
                    val childHeightMeasureSpec: Int

                    if (lp.width == FrameLayout.LayoutParams.MATCH_PARENT) {
                        childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                            measuredWidth -
                                    paddingLeft - paddingRight -
                                    lp.leftMargin - lp.rightMargin,
                            View.MeasureSpec.EXACTLY
                        )
                    } else {
                        childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(
                            widthMeasureSpec,
                            paddingLeft + paddingRight +
                                    lp.leftMargin + lp.rightMargin,
                            lp.width
                        )
                    }

                    if (lp.height == FrameLayout.LayoutParams.MATCH_PARENT) {
                        childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                            measuredHeight -
                                    paddingTop - paddingBottom -
                                    lp.topMargin - lp.bottomMargin,
                            View.MeasureSpec.EXACTLY
                        )
                    } else {
                        childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(
                            heightMeasureSpec,
                            paddingTop + paddingBottom +
                                    lp.topMargin + lp.bottomMargin,
                            lp.height
                        )
                    }

                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
                }
            }
        } catch (e: Exception) {
            //            FileLog.e("tmessages", e);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

    }

    companion object {

        fun resolveSizeAndStateFixed(size: Int, measureSpec: Int, childMeasuredState: Int): Int {
            var result = size
            val specMode = View.MeasureSpec.getMode(measureSpec)
            val specSize = View.MeasureSpec.getSize(measureSpec)
            when (specMode) {
                View.MeasureSpec.UNSPECIFIED -> result = size
                View.MeasureSpec.AT_MOST -> if (specSize < size) {
                    result = specSize or 0x01000000
                } else {
                    result = size
                }
                View.MeasureSpec.EXACTLY -> result = specSize
            }
            return result or (childMeasuredState and -0x1000000)
        }
    }
}
