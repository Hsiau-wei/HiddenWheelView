package me.tictok.hiddenwheelviewlibrary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class CustomRecyclerView extends RecyclerView {
    public CustomRecyclerView(@NonNull Context context) {
        super(context);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void attachSnapHelperWithListener(CustomSnapHelper snapHelper, OnSnapPositionChangeListener listener, SnapOnScrollListener.Behavior behavior) {
        snapHelper.attachToRecyclerView(this);
        addOnScrollListener(new SnapOnScrollListener(snapHelper, listener, behavior));
    }
}
