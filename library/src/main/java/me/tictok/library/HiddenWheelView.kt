package me.tictok.library

import android.animation.ValueAnimator
import android.app.Service
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import android.widget.FrameLayout

class HiddenWheelView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {

    private var mItemHeight: Int
    private var mItemCount: Int

    private var mAllowVibrate: Boolean
    private var mCanVibrate = false
    private var mVibrator: Vibrator? = null
    private var mVibrationEffect: VibrationEffect? = null
    private var mIsVibrating = false

    private var mPaint: Paint
    private var mShaderTop: Shader? = null
    private var mShaderBottom: Shader? = null
    private val mRectTop: Rect = Rect()
    private val mRectBottom: Rect = Rect()
    private var mPadding: Int = 0

    private val mRecyclerView: CustomRecyclerView
    private val mLayoutManager: RecyclerView.LayoutManager
    private val mRecyclerViewParams: LayoutParams
    private val snapHelper: CustomSnapHelper
    private var mScrollToTop = false

    private var mOffset = 0f

    private var mAnimShow: ValueAnimator? = null
    private var mAnimHide: ValueAnimator? = null
    private var mInterpolator: PathInterpolator
    private val mAnimationDuration: Long

    var onSnapListener: OnSnapListener? = null

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.HiddenWheelView)
        mItemHeight = typedArray.getDimension(R.styleable.HiddenWheelView_itemHeight,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, context.resources.displayMetrics)).toInt()
        mItemCount = typedArray.getInt(R.styleable.HiddenWheelView_itemCount, 3)
        mAllowVibrate = typedArray.getBoolean(R.styleable.HiddenWheelView_allowVibrate, false)
        mAnimationDuration = typedArray.getInt(R.styleable.HiddenWheelView_animationDuration, 500).toLong()
        typedArray.recycle()

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        }

        if (mAllowVibrate) {
            mVibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mVibrationEffect = VibrationEffect.createOneShot(5, 40)
            }
        }

        mRecyclerView = CustomRecyclerView(context, attributeSet)
        mRecyclerView.id = View.generateViewId()
        mLayoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.clipToPadding = false
        snapHelper = CustomSnapHelper()
        mRecyclerView.attachSnapHelperWithListener(snapHelper, object : OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                // do nothing
            }

            override fun onStopScrolling() {
                setHideAnimation()
                mAnimShow?.pause()
                mAnimHide?.start()
                onSnapListener?.onStopScrolling()
            }

        }, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE)

        mRecyclerView.attachSnapHelperWithListener(snapHelper, object : OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                if (mCanVibrate) {
                    Thread(Runnable {
                        if (!mIsVibrating) {
                            mIsVibrating = true
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                mVibrator?.vibrate(mVibrationEffect)
                            } else mVibrator?.vibrate(2)
                            mIsVibrating = false
                        }
                    }).start()
                }
                onSnapListener?.onSnap(position)
            }

            override fun onStopScrolling() {
                // do nothing
            }
        }, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL)

        mRecyclerViewParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        snapHelper.attachToRecyclerView(mRecyclerView)
        addView(mRecyclerView, mRecyclerViewParams)

        mInterpolator = PathInterpolator(0.04f, 0f, 0.2f, 1f)
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mRecyclerView.adapter = adapter
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                mRecyclerViewParams.height = mItemCount * mItemHeight
                mRecyclerView.layoutParams = mRecyclerViewParams
        }
    }

    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        val layer = canvas!!.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        val draw = super.drawChild(canvas, child, drawingTime)
        mPadding = (mRecyclerView.height - mItemHeight) / 2
        mRecyclerView.setPadding(0, mPadding, 0, mPadding)
        if (!mScrollToTop) {
            mLayoutManager.scrollToPosition(0)
            mScrollToTop = true
        }
        mShaderTop = LinearGradient(0f, mPadding - mOffset, 0f, mPadding * 2 - mOffset, 0xff000000.toInt(), 0x00000000, Shader.TileMode.CLAMP)
        mPaint.shader = mShaderTop
        canvas.drawRect(mRectTop.apply {
            left = 0
            top = 0
            right = width
            bottom = mPadding
        }, mPaint)
        canvas.save()
        mShaderBottom = LinearGradient(0f, height - mPadding - mPadding + mOffset, 0f, height - mPadding + mOffset, 0x00000000, 0xff000000.toInt(), Shader.TileMode.CLAMP)
        mPaint.shader = mShaderBottom
        canvas.drawRect(mRectBottom.apply {
            left = 0
            top = height - mPadding
            right = width
            bottom = height
        }, mPaint)
        canvas.restore()
        canvas.restoreToCount(layer)
        return draw
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        mCanVibrate = true
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                setShowAnimation()
                mAnimHide?.pause()
                mAnimShow?.start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                setHideAnimation()
                mAnimShow?.pause()
                mAnimHide?.start()
            }
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_CANCEL) {
            setHideAnimation()
            mAnimShow?.pause()
            mAnimHide?.start()
        }
        return true
    }

    interface OnSnapListener {
        fun onSnap(position: Int)
        fun onStopScrolling()
    }

    private fun setShowAnimation() {
        mAnimShow = ValueAnimator.ofFloat(mOffset, mPadding.toFloat()).apply {
            duration = (mAnimationDuration * (mPadding - mOffset) / mPadding).toLong()
            repeatCount = 0
            interpolator = mInterpolator
            addUpdateListener {
                mOffset = it.animatedValue as Float
                invalidate()
            }
        }
    }

    private fun setHideAnimation() {
        mAnimHide = ValueAnimator.ofFloat(mOffset, 0f).apply {
            duration = (mAnimationDuration * mOffset / mPadding).toLong()
            repeatCount = 0
            interpolator = mInterpolator
            addUpdateListener {
                mOffset = it.animatedValue as Float
                invalidate()
            }
        }
    }
}