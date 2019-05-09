package me.tictok.library

import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView

class CustomSnapHelper : LinearSnapHelper() {

    fun getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }
}
