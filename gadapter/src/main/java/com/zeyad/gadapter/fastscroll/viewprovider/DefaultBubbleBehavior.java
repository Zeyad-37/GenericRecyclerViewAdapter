package com.zeyad.gadapter.fastscroll.viewprovider;

class DefaultBubbleBehavior implements ViewBehavior {

    private final VisibilityAnimationManager animationManager;

    DefaultBubbleBehavior(VisibilityAnimationManager animationManager) {
        this.animationManager = animationManager;
    }

    @Override
    public void onHandleGrabbed() {
        animationManager.show();
    }

    @Override
    public void onHandleReleased() {
        animationManager.hide();
    }

    @Override
    public void onScrollStarted() {
    }

    @Override
    public void onScrollFinished() {
    }
}
