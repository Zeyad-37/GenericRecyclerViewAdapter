package com.zeyad.gadapter.fastscroll.viewprovider

internal class DefaultBubbleBehavior(private val animationManager: VisibilityAnimationManager) : ViewBehavior {

    override fun onHandleGrabbed() {
        animationManager.show()
    }

    override fun onHandleReleased() {
        animationManager.hide()
    }

    override fun onScrollStarted() {}

    override fun onScrollFinished() {}
}
