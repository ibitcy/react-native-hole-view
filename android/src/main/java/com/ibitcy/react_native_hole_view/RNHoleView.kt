package com.ibitcy.react_native_hole_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout


class RNHoleView: FrameLayout {

    class Hole(var x: Int, var y: Int, var width: Int, var height: Int, var borderRadius: Int = 0)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var holesPath: Path? = null
    private val holesPaint: Paint

    init {
        isClickable = false
        isFocusable = false

        this.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        holesPaint = Paint()
        holesPaint.color = Color.TRANSPARENT
        holesPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun setHoles(holes: List<Hole>) {
        holesPath = Path()
        holes.forEach { hole ->
            holesPath!!.addRoundRect(RectF(
                    hole.x.toFloat(),
                    hole.y.toFloat(),
                    hole.width.toFloat() + hole.x.toFloat(),
                    hole.height.toFloat() + hole.y.toFloat()),
                    hole.borderRadius.toFloat(),
                    hole.borderRadius.toFloat(),
                    Path.Direction.CW
            )
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (holesPath != null) {
            canvas?.drawPath(holesPath!!, holesPaint)
        }
    }

    private fun isTouchInsideHole(event: MotionEvent): Boolean {
        if (holesPath == null)
            return false
        val clickableRegion = Region()
        val rectF = RectF()
        holesPath!!.computeBounds(rectF, true)
        val rect = Rect(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
        clickableRegion.setPath(holesPath!!, Region(rect))
        return clickableRegion.contains(event.x.toInt(), event.y.toInt())
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val inside = isTouchInsideHole(event)
        if (inside) {
            performClick()
        }
        return !inside
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return !isTouchInsideHole(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return !isTouchInsideHole(event)
    }

    override fun onInterceptHoverEvent(event: MotionEvent): Boolean {
        return !isTouchInsideHole(event)
    }
}