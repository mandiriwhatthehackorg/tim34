/*
 * This is the source code of Telegram for Android v. 1.7.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2014.
 */

package com.mandiri.whatthehack.audiovideo.other

import android.view.View

class ViewProxy {

    fun setRotationX(view: View, rotationX: Float) {
        if (View10.NEED_PROXY) {
            View10.wrap(view).rotationX = rotationX
        } else {
            view.rotationX = rotationX
        }
    }

    fun setRotationY(view: View, rotationY: Float) {
        if (View10.NEED_PROXY) {
            View10.wrap(view).rotationY = rotationY
        } else {
            view.rotationY = rotationY
        }
    }

    companion object {

        fun getAlpha(view: View): Float {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).alpha
            } else {
                view.alpha
            }
        }

        fun setAlpha(view: View, alpha: Float) {
            if (View10.NEED_PROXY) {
                View10.wrap(view).alpha = alpha
            } else {
                view.alpha = alpha
            }
        }

        fun getPivotX(view: View): Float {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).pivotX
            } else {
                view.pivotX
            }
        }

        fun setPivotX(view: View, pivotX: Float) {
            if (View10.NEED_PROXY) {
                View10.wrap(view).pivotX = pivotX
            } else {
                view.pivotX = pivotX
            }
        }

        fun getPivotY(view: View): Float {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).pivotY
            } else {
                view.pivotY
            }
        }

        fun setPivotY(view: View, pivotY: Float) {
            if (View10.NEED_PROXY) {
                View10.wrap(view).pivotY = pivotY
            } else {
                view.pivotY = pivotY
            }
        }

        fun getRotation(view: View): Float {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).rotation
            } else {
                view.rotation
            }
        }

        fun setRotation(view: View, rotation: Float) {
            if (View10.NEED_PROXY) {
                View10.wrap(view).rotation = rotation
            } else {
                view.rotation = rotation
            }
        }

        fun getRotationX(view: View): Float {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).rotationX
            } else {
                view.rotationX
            }
        }

        fun getRotationY(view: View): Float {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).rotationY
            } else {
                view.rotationY
            }
        }

        fun getScaleX(view: View): Float {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).scaleX
            } else {
                view.scaleX
            }
        }

        fun setScaleX(view: View, scaleX: Float) {
            if (View10.NEED_PROXY) {
                View10.wrap(view).scaleX = scaleX
            } else {
                view.scaleX = scaleX
            }
        }

        fun getScaleY(view: View): Float {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).scaleY
            } else {
                view.scaleY
            }
        }

        fun setScaleY(view: View, scaleY: Float) {
            if (View10.NEED_PROXY) {
                View10.wrap(view).scaleY = scaleY
            } else {
                view.scaleY = scaleY
            }
        }

        fun getScrollX(view: View): Int {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).scrollX
            } else {
                view.scrollX
            }
        }

        fun setScrollX(view: View, value: Int) {
            if (View10.NEED_PROXY) {
                View10.wrap(view).scrollX = value
            } else {
                view.scrollX = value
            }
        }

        fun getScrollY(view: View): Int {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).scrollY
            } else {
                view.scrollY
            }
        }

        fun setScrollY(view: View, value: Int) {
            if (View10.NEED_PROXY) {
                View10.wrap(view).scrollY = value
            } else {
                view.scrollY = value
            }
        }

        fun getTranslationX(view: View): Float {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).translationX
            } else {
                view.translationX
            }
        }

        fun setTranslationX(view: View, translationX: Float) {
            if (View10.NEED_PROXY) {
                View10.wrap(view).translationX = translationX
            } else {
                view.translationX = translationX
            }
        }

        fun getTranslationY(view: View): Float {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).translationY
            } else {
                view.translationY
            }
        }

        fun setTranslationY(view: View, translationY: Float) {
            if (View10.NEED_PROXY) {
                View10.wrap(view).translationY = translationY
            } else {
                view.translationY = translationY
            }
        }

        fun getX(view: View): Float {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).x
            } else {
                view.x
            }
        }

        fun setX(view: View, x: Float) {
            if (View10.NEED_PROXY) {
                View10.wrap(view).x = x
            } else {
                view.x = x
            }
        }

        fun getY(view: View): Float {
            return if (View10.NEED_PROXY) {
                View10.wrap(view).y
            } else {
                view.y
            }
        }

        fun setY(view: View, y: Float) {
            if (View10.NEED_PROXY) {
                View10.wrap(view).y = y
            } else {
                view.y = y
            }
        }

        fun wrap(view: View): Any {
            return if (View10.NEED_PROXY) {
                View10.wrap(view)
            } else {
                view
            }
        }
    }
}
