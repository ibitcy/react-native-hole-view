package com.ibitcy.react_native_hole_view

import android.content.Context
import android.graphics.*
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

    class Hole(var x: Int, var y: Int, var width: Int, var height: Int, var borderRadius: Int = 0)

    private var mHolesPath: Path? = null
    private val mHolesPaint: Paint

    private val mEventDispatcher: EventDispatcher

    init {
        isClickable = false
        isFocusable = false

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
            mHolesPath!!.addRoundRect(RectF(
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
        if (mHolesPath != null) {
            canvas?.drawPath(mHolesPath!!, mHolesPaint)
        }
    }

    private fun isTouchInsideHole(event: MotionEvent): Boolean {
        if (mHolesPath == null)
            return false
        val clickableRegion = Region()
        val rectF = RectF()
        mHolesPath!!.computeBounds(rectF, true)
        val rect = Rect(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
        clickableRegion.setPath(mHolesPath!!, Region(rect))
        return clickableRegion.contains(event.x.toInt(), event.y.toInt())
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val inside = isTouchInsideHole(event)
        if (inside) {
            performClick()
            return false
        }
        return !inside
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        super.onInterceptTouchEvent(ev)
        return isTouchInsideHole(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val inside = isTouchInsideHole(ev)
        if (inside) {
            passTouchEventToViewAndChildren(getRoot(), ev)
        }
        return !inside
    }

    private fun getRoot(): ViewGroup {
        return ((rootView.findViewById(android.R.id.content) as ViewGroup))/*.getChildAt(0)) as ViewGroup*/
    }

    private fun passTouchEventToViewAndChildren(v: ViewGroup, ev: MotionEvent) {
        val childrenCount = v.childCount
        for (i in 0 until childrenCount) {
            val child = v.getChildAt(i)
            if (child.id > 0 && isViewInsideTouch(ev, child)) {
                mEventDispatcher.dispatchEvent(
                        TouchEvent.obtain(
                                child.id,
                                TouchEventType.START,
                                ev,
                                ev.eventTime,
                                ev.x,
                                ev.y,
                                TouchEventCoalescingKeyHelper()))
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