package me.tictok.hiddenwheelviewlibrary;

import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class HiddenWheelView extends FrameLayout {

    private int mItemHeight;
    private int mItemCount;

    private Paint mPaint;
    private Shader mShaderTop;
    private Shader mShaderBottom;

    private Vibrator mVibrator;
    private VibrationEffect mVibrationEffect;
    private boolean mAllowVibrate;
    private boolean mIsVibrating = false;

    private CustomRecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FrameLayout.LayoutParams mRecyclerViewParams;
    private CustomSnapHelper snapHelper;
    private boolean mScrollToTop = false;

    private OnSnapListener mOnSnapListener;

    public HiddenWheelView(Context context) {
        this(context, null);
    }

    public HiddenWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray1 = context.obtainStyledAttributes(attrs, R.styleable.HiddenWheelView);
        mItemHeight = (int) typedArray1.getDimension(R.styleable.HiddenWheelView_itemHeight,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, context.getResources().getDisplayMetrics()));
        mItemCount = typedArray1.getInt(R.styleable.HiddenWheelView_itemCount, 3);
        mAllowVibrate = typedArray1.getBoolean(R.styleable.HiddenWheelView_allowVibrate, false);
        typedArray1.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        if (mAllowVibrate) {
            mVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mVibrationEffect = VibrationEffect.createOneShot(5, 40);
            }
        }

        mRecyclerView = new CustomRecyclerView(context, attrs);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setClipToPadding(true);
        snapHelper = new CustomSnapHelper();
        mRecyclerView.attachSnapHelperWithListener(snapHelper, new OnSnapPositionChangeListener() {
            @Override
            public void onSnapPositionChange(int position) {

            }

            @Override
            public void onStopScrolling() {
                mRecyclerView.setClipToPadding(true);
                if (mOnSnapListener != null) {
                    mOnSnapListener.onStopScrolling();
                }
            }
        }, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE);
        mRecyclerView.attachSnapHelperWithListener(snapHelper, new OnSnapPositionChangeListener() {
            @Override
            public void onSnapPositionChange(int position) {
                if (mAllowVibrate) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mIsVibrating) {
                                mIsVibrating = true;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    mVibrator.vibrate(mVibrationEffect);
                                } else {
                                    mVibrator.vibrate(2);
                                }
                                mIsVibrating = false;
                            }
                        }
                    }).start();
                }
                if (mOnSnapListener != null) {
                    mOnSnapListener.onSnap(position);
                }
            }

            @Override
            public void onStopScrolling() {

            }
        }, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL);
        mRecyclerViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        snapHelper.attachToRecyclerView(mRecyclerView);
        addView(mRecyclerView, mRecyclerViewParams);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        switch (getLayoutParams().height) {
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                mRecyclerViewParams.height = mItemCount * mItemHeight;
                mRecyclerView.setLayoutParams(mRecyclerViewParams);
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int layer = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        boolean draw = super.drawChild(canvas, child, drawingTime);
        int padding = (mRecyclerView.getHeight() - mItemHeight) / 2;
        mRecyclerView.setPadding(0, padding, 0, padding);
        if (!mScrollToTop) {
            mLayoutManager.scrollToPosition(0);
            mScrollToTop = true;
        }
        mShaderTop = new LinearGradient(0, 0, 0, padding, 0xff000000, 0x00000000, Shader.TileMode.CLAMP);
        mPaint.setShader(mShaderTop);
        canvas.drawPaint(mPaint);
        canvas.save();
        mShaderBottom = new LinearGradient(0, getHeight() - padding, 0, getHeight(), 0x00000000, 0xff000000, Shader.TileMode.CLAMP);
        mPaint.setShader(mShaderBottom);
        canvas.drawPaint(mPaint);
        canvas.restore();
        canvas.restoreToCount(layer);
        return draw;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mRecyclerView.setClipToPadding(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mRecyclerView.setClipToPadding(true);
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL) {
            mRecyclerView.setClipToPadding(true);
        }
        return true;
    }

    public void setOnSnapListener(OnSnapListener listener) {
        mOnSnapListener = listener;
    }

    public interface OnSnapListener {
        public void onSnap(int position);

        public void onStopScrolling();
    }
}
