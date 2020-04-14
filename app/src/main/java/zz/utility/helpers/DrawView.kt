package zz.utility.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import zz.utility.R

class DrawView : View {
    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()

    private val mPath = Path()
    private val mPaint = Paint()

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setPenSize()
    }

    constructor(c: Context) : super(c) {
        setPenSize()
    }

    fun clearSpace() {
        mPath.reset()
        invalidate()
    }

    private fun setPenSize() {
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(mPath, mPaint)
    }

    private fun touchStart(x: Float, y: Float) {
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        mPath.quadTo(mX, mY, x, y)
        mX = x
        mY = y
    }

    @SuppressLint("SetTextI18n")
    private fun drawEvents(event: MotionEvent) {
        (0 until event.historySize).forEach { touchMove(event.getHistoricalX(it), event.getHistoricalY(it)) }
        touchMove(event.x, event.y)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean = consume {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart(event.x, event.y)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_MOVE -> drawEvents(event)
        }
        invalidate()
    }

//    fun compress(): ByteArray {
//        val drawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        val mCanvas = Canvas(drawBitmap!!)
//        mCanvas.drawPath(mPath, mPaint)
//        val stream = ByteArrayOutputStream()
//        drawBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//        return stream.toByteArray()
//    }
}
