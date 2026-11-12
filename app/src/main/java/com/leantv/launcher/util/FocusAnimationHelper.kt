package com.leantv.launcher.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator

/**
 * TV D-Pad Focus Animator.
 * Applies a smooth scale-up (1.08x) and subtle elevation boost when a card receives remote focus,
 * smoothly returning to 1.0x when focus leaves.
 */
object FocusAnimationHelper {

    private const val FOCUSED_SCALE = 1.08f
    private const val UNFOCUSED_SCALE = 1.0f
    private const val ANIMATION_DURATION_MS = 150L

    fun applyFocusScale(view: View, hasFocus: Boolean) {
        val targetScale = if (hasFocus) FOCUSED_SCALE else UNFOCUSED_SCALE
        val targetElevation = if (hasFocus) 12f else 0f

        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, targetScale)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, targetScale)
        val elevation = ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, targetElevation)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, elevation)
            duration = ANIMATION_DURATION_MS
            interpolator = DecelerateInterpolator()
            start()
        }
    }
}

