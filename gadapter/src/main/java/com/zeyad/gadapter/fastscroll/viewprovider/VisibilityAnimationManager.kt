package com.zeyad.gadapter.fastscroll.viewprovider

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.support.annotation.AnimatorRes
import android.view.View

import com.zeyad.gadapter.R
import com.zeyad.gadapter.fastscroll.FastScroller

/**
 * Animates showing and hiding elements of the [FastScroller] (handle and bubble).
 * The decision when to show/hide the element should be implemented via [ViewBehavior].
 */
internal class VisibilityAnimationManager private constructor(protected val view: View,
                                                              @AnimatorRes showAnimator: Int,
                                                              @AnimatorRes hideAnimator: Int,
                                                              private val pivotXRelative: Float,
                                                              private val pivotYRelative: Float,
                                                              hideDelay: Int) {

    private val hideAnimator: AnimatorSet = AnimatorInflater.loadAnimator(view.context, hideAnimator) as AnimatorSet
    private val showAnimator: AnimatorSet

    init {
        this.hideAnimator.startDelay = hideDelay.toLong()
        this.hideAnimator.setTarget(view)
        this.showAnimator = AnimatorInflater.loadAnimator(view.context, showAnimator) as AnimatorSet
        this.showAnimator.setTarget(view)
        this.hideAnimator.addListener(object : AnimatorListenerAdapter() {
            //because onAnimationEnd() goes off even for canceled animations
            var wasCanceled: Boolean = false

            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                wasCanceled = true
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (!wasCanceled)
                    view.visibility = View.INVISIBLE
                wasCanceled = false
            }
        })
        updatePivot()
    }

    fun show() {
        hideAnimator.cancel()
        if (view.visibility == View.INVISIBLE) {
            view.visibility = View.VISIBLE
            updatePivot()
            showAnimator.start()
        }
    }

    fun hide() {
        updatePivot()
        hideAnimator.start()
    }

    private fun updatePivot() {
        view.pivotX = pivotXRelative * view.measuredWidth
        view.pivotY = pivotYRelative * view.measuredHeight
    }

    internal abstract class AbsBuilder<T : VisibilityAnimationManager>(protected val view: View) {
        var showAnimatorResource = R.animator.fastscroll__default_show
        var hideAnimatorResource = R.animator.fastscroll__default_hide
        var hideDelay = 1000
        var pivotX = 0.5f
        var pivotY = 0.5f

        fun withShowAnimator(@AnimatorRes showAnimatorResource: Int): AbsBuilder<T> {
            this.showAnimatorResource = showAnimatorResource
            return this
        }

        fun withHideAnimator(@AnimatorRes hideAnimatorResource: Int): AbsBuilder<T> {
            this.hideAnimatorResource = hideAnimatorResource
            return this
        }

        fun withHideDelay(hideDelay: Int): AbsBuilder<T> {
            this.hideDelay = hideDelay
            return this
        }

        fun withPivotX(pivotX: Float): AbsBuilder<T> {
            this.pivotX = pivotX
            return this
        }

        fun withPivotY(pivotY: Float): AbsBuilder<T> {
            this.pivotY = pivotY
            return this
        }

        abstract fun build(): T
    }

    internal class Builder(view: View) : AbsBuilder<VisibilityAnimationManager>(view) {

        override fun build(): VisibilityAnimationManager {
            return VisibilityAnimationManager(view, showAnimatorResource, hideAnimatorResource, pivotX, pivotY, hideDelay)
        }
    }
}
