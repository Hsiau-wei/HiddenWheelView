package me.tictok.library

import android.support.v7.widget.RecyclerView

class SnapOnScrollListener(private val snapHelper: CustomSnapHelper, private var onSnapPositionChangeListener: OnSnapPositionChangeListener, private var behavior: Behavior) : RecyclerView.OnScrollListener() {
    private var snapPosition = RecyclerView.NO_POSITION

    enum class Behavior {
        NOTIFY_ON_SCROLL,
        NOTIFY_ON_SCROLL_STATE_IDLE
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL) {
            maybeNotifySnapPositionChange(recyclerView)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL_STATE_IDLE && newState == RecyclerView.SCROLL_STATE_IDLE) {
            maybeNotifySnapPositionChange(recyclerView)
            onSnapPositionChangeListener.onStopScrolling()
        }
    }

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPosition(recyclerView)
        val snapPositionChanged = this.snapPosition != snapPosition
        if (snapPositionChanged) {
            onSnapPositionChangeListener.onSnapPositionChange(snapPosition)
            this.snapPosition = snapPosition
        }
    }
}
