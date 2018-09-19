package zz.utility.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

class DrawView : View {
    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()

    private val mPath = Path()
    private val mPaint = Paint()
    var currentData: TextView? = null

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setPenSize()
    }

    constructor(c: Context) : super(c) {
        setPenSize()
    }

    private fun setPenSize() {
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        isDrawingCacheEnabled = true
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
        currentData?.text = "%.0f/%.0f => %.3f (%.3f)".format(event.x, event.y, event.pressure, event.size)
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
