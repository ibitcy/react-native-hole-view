package com.ibitcy.react_native_hole_view

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.UIManagerModule
import com.facebook.react.uimanager.events.EventDispatcher
import com.facebook.react.uimanager.events.TouchEvent
import com.facebook.react.uimanager.events.TouchEventCoalescingKeyHelper
import com.facebook.react.uimanager.events.TouchEventType
import com.facebook.react.views.view.ReactViewGroup


class RNHoleView(context: Context) : ReactViewGroup(context) {

    class Hole(
            var x: Int,
            var y: Int,
            var width: Int,
            var height: Int,
            var borderTopLeftRadius: Int = 0,
            var borderTopRightRadius: Int = 0,
            var borderBottomLeftRadius: Int = 0,
            var borderBottomRightRadius: Int = 0
    )

    private var mHolesPath: Path? = null
    private val mHolesPaint: Paint

    private val mEventDispatcher: EventDispatcher

    init {
        this.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        mHolesPaint = Paint()
        mHolesPaint.color = Color.TRANSPARENT
        mHolesPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        val uiManager = (context as ReactContext).getNativeModule(UIManagerModule::class.java)
        mEventDispatcher = uiManager!!.eventDispatcher
    }

    fun setHoles(holes: List<Hole>) {
        mHolesPath = Path()
        holes.forEach { hole ->
            val radii = floatArrayOf(
                    hole.borderTopLeftRadius.toFloat(),
                    hole.borderTopLeftRadius.toFloat(),
                    hole.borderTopRightRadius.toFloat(),
                    hole.borderTopRightRadius.toFloat(),
                    hole.borderBottomRightRadius.toFloat(),
                    hole.borderBottomRightRadius.toFloat(),
                    hole.borderBottomLeftRadius.toFloat(),
                    hole.borderBottomLeftRadius.toFloat()
            )


            Log.d("12345", "hole " + hole.x + " " + hole.y)

            radii.forEach {
                Log.d("12345", "rad " + it)
            }


            mHolesPath!!.addRoundRect(RectF(
                    hole.x.toFloat(),
                    hole.y.toFloat(),
                    hole.width.toFloat() + hole.x.toFloat(),
                    hole.height.toFloat() + hole.y.toFloat()),
                    radii,
                    Path.Direction.CW
            )
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mHolesPath != null) {
            canvas?.drawPath(mHolesPath!!, mHolesPaint)
        }
    }

    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        super.drawChild(canvas, child, drawingTime)
        if (mHolesPath != null) {
            canvas?.drawPath(mHolesPath!!, mHolesPaint)
        }
        return true
    }

    private fun isTouchInsideHole( touchX:Int, touchY:Int): Boolean {
        if (mHolesPath == null)
            return false
        val clickableRegion = Region()
        val rectF = RectF()
        mHolesPath!!.computeBounds(rectF, true)
        val rect = Rect(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
        clickableRegion.setPath(mHolesPath!!, Region(rect))
        return clickableRegion.contains(touchX, touchY)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val inside = isTouchInsideHole(event.x.toInt(), event.y.toInt())
        if (inside) {
            return false
        }
        return !inside
    }

//    We'll need it in case Facebook will accept our PR https://github.com/facebook/react-native/issues/28953
//    override fun onJSTouchEvent(x: Float, y: Float): Boolean {
//        val inside = isTouchInsideHole(x.toInt(),y.toInt())
//        if (inside) {
//            performClick()
//            return false
//        }
//
//        return super.onJSTouchEvent(x, y)
//    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        super.onInterceptTouchEvent(ev)
        return isTouchInsideHole(ev.x.toInt(), ev.y.toInt())
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val inside = isTouchInsideHole(ev.x.toInt(), ev.y.toInt())
        if (inside) {
            passTouchEventToViewAndChildren(getRoot(), ev)
        }
        return !inside
    }

    private fun getRoot(): ViewGroup {
        return parent as ViewGroup
    }

    private fun passTouchEventToViewAndChildren(v: ViewGroup, ev: MotionEvent) {
        val childrenCount = v.childCount
        for (i in 0 until childrenCount) {
            val child = v.getChildAt(i)
            if (child.id > 0 && isViewInsideTouch(ev, child) && child.visibility == View.VISIBLE) {
                try {
                    mEventDispatcher.dispatchEvent(
                            TouchEvent.obtain(
                                    child.id,
                                    TouchEventType.START,
                                    ev,
                                    ev.eventTime,
                                    ev.x,
                                    ev.y,
                                    TouchEventCoalescingKeyHelper()))
                } catch (e: Exception) {}
                if (child is ViewGroup && child.childCount > 0) {
                    passTouchEventToViewAndChildren(child, ev)
                }
            }
        }
    }

    private fun isViewInsideTouch(event: MotionEvent, view: View): Boolean {
        val viewRegion = Region()
        val xy = IntArray(2)
        view.getLocationInWindow(xy)
        val x = xy[0]
        val y = xy[1]
        val rect = Rect(x, y, x + view.width, y + view.height)
        viewRegion.set(rect)
        return viewRegion.contains(event.rawX.toInt(), event.rawY.toInt())
    }
}