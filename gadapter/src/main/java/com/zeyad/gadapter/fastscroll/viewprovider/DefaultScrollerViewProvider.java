package com.zeyad.gadapter.fastscroll.viewprovider;

import android.graphics.drawable.InsetDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeyad.gadapter.R;
import com.zeyad.gadapter.fastscroll.Utils;

public class DefaultScrollerViewProvider extends ScrollerViewProvider {

    private View bubble;
    private View handle;

    @Override
    public View provideHandleView(ViewGroup container) {
        handle = new View(getContext());
        int verticalInset = getScroller().isVertical() ? 0 : getContext().getResources().getDimensionPixelSize(R.dimen.fastscroll_handle_inset);
        int horizontalInset = !getScroller().isVertical() ? 0 : getContext().getResources().getDimensionPixelSize(R.dimen.fastscroll_handle_inset);
        InsetDrawable handleBg = new InsetDrawable(ContextCompat.getDrawable(getContext(), R.drawable.fastscroll_default_handle), horizontalInset,
                verticalInset, horizontalInset, verticalInset);
        Utils.setBackground(handle, handleBg);
        int handleWidth = getContext().getResources().getDimensionPixelSize(
                getScroller().isVertical() ? R.dimen.fastscroll_handle_clickable_width : R.dimen.fastscroll_handle_height);
        int handleHeight = getContext().getResources().getDimensionPixelSize(
                getScroller().isVertical() ? R.dimen.fastscroll_handle_height : R.dimen.fastscroll_handle_clickable_width);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(handleWidth, handleHeight);
        handle.setLayoutParams(params);
        return handle;
    }

    @Override
    public View provideBubbleView(ViewGroup container) {
        bubble = LayoutInflater.from(getContext()).inflate(R.layout.fastscroll_default_bubble, container, false);
        return bubble;
    }

    @Override
    public TextView provideBubbleTextView() {
        return (TextView) bubble;
    }

    @Override
    public int getBubbleOffset() {
        return (int) (
                getScroller().isVertical() ? ((float) handle.getHeight() / 2f) - bubble.getHeight()
                                           : ((float) handle.getWidth() / 2f) - bubble.getWidth());
    }

    @Override
    protected ViewBehavior provideHandleBehavior() {
        return null;
    }

    @Override
    protected ViewBehavior provideBubbleBehavior() {
        return new DefaultBubbleBehavior(new VisibilityAnimationManager.Builder(bubble).withPivotX(1f).withPivotY(1f).build());
    }
}
