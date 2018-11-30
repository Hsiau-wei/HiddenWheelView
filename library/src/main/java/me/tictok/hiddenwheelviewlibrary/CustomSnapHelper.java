package me.tictok.hiddenwheelviewlibrary;

import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CustomSnapHelper extends LinearSnapHelper {

    public int getSnapPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) return RecyclerView.NO_POSITION;
        View snapView = findSnapView(layoutManager);
        if (snapView == null) return RecyclerView.NO_POSITION;
        return layoutManager.getPosition(snapView);
    }
}
