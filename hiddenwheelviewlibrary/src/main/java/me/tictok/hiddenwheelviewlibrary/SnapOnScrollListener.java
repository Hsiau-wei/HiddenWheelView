package me.tictok.hiddenwheelviewlibrary;

import android.support.v7.widget.RecyclerView;

public class SnapOnScrollListener extends RecyclerView.OnScrollListener {

    private CustomSnapHelper snapHelper;
    OnSnapPositionChangeListener onSnapPositionChangeListener;
    Behavior behavior;
    enum Behavior {
        NOTIFY_ON_SCROLL,
        NOTIFY_ON_SCROLL_STATE_IDLE
    }
    private int snapPosition = RecyclerView.NO_POSITION;

    public SnapOnScrollListener(CustomSnapHelper snapHelper, OnSnapPositionChangeListener onSnapPositionChangeListener, Behavior behavior) {
        this.snapHelper = snapHelper;
        this.onSnapPositionChangeListener = onSnapPositionChangeListener;
        this.behavior = behavior;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL) {
            maybeNotifySnapPositionChange(recyclerView);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL_STATE_IDLE
                && newState == RecyclerView.SCROLL_STATE_IDLE) {
            maybeNotifySnapPositionChange(recyclerView);
            onSnapPositionChangeListener.onStopScrolling();
        }
    }

    private void maybeNotifySnapPositionChange(RecyclerView recyclerView) {
        int snapPosition = snapHelper.getSnapPosition(recyclerView);
        boolean snapPositionChanged = this.snapPosition != snapPosition;
        if (snapPositionChanged) {
            onSnapPositionChangeListener.onSnapPositionChange(snapPosition);
            this.snapPosition = snapPosition;
        }
    }
}
