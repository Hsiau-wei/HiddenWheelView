package me.tictok.library

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class CustomRecyclerView : RecyclerView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    fun attachSnapHelperWithListener(snapHelper: CustomSnapHelper, listener: OnSnapPositionChangeListener, behavior: SnapOnScrollListener.Behavior) {
        snapHelper.attachToRecyclerView(this)
        addOnScrollListener(SnapOnScrollListener(snapHelper, listener, behavior))
    }
}
