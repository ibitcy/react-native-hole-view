package com.ibitcy.react_native_hole_view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.RectEvaluator
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.view.animation.Interpolator
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.UIManagerModule
import com.facebook.react.uimanager.events.EventDispatcher
import com.facebook.react.uimanager.events.TouchEvent
import com.facebook.react.uimanager.events.TouchEventCoalescingKeyHelper
import com.facebook.react.uimanager.events.TouchEventType
import com.facebook.react.views.view.ReactViewGroup


class RNHoleView(context: Context) : ReactViewGroup(context) {
    companion object {
        const val ANIMATION_DURATION_DEFAULT = 1000L

        private val sRectEvaluator = RectEvaluator()

        private fun getAnimationInterpolator(type: EAnimationTimingFunction): Interpolator {
            return when (type) {
                EAnimationTimingFunction.LINEAR -> LinearInterpolator()
                EAnimationTimingFunction.EASE_IN -> AccelerateInterpolator()
                EAnimationTimingFunction.EASE_OUT -> DecelerateInterpolator()
                EAnimationTimingFunction.EASE_IN_OUT -> AccelerateDecelerateInterpolator()
            }
        }
    }

    class Hole(
            var x: Int,
            var y: Int,
            var width: Int,
            var height: Int,
            var borderTopLeftRadius: Int = 0,
            var borderTopRightRadius: Int = 0,
            var borderBottomLeftRadius: Int = 0,
            var borderBottomRightRadius: Int = 0,
            var rect: Rect? = null
    )

    class Animation(
            var duration: Long = ANIMATION_DURATION_DEFAULT,
            var timingFunction: EAnimationTimingFunction
    )

    enum class EAnimationTimingFunction(val type: String) {
        LINEAR("LINEAR"),
        EASE_IN("EASE_IN"),
        EASE_OUT("EASE_OUT"),
        EASE_IN_OUT("EASE_IN_OUT"),
    }

    var animation: Animation? = null
    var onAnimationFinished: (() -> Unit)? = null

    private var mHolesPath: Path? = null
    private val mHolesPaint: Paint

    init {
        this.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        mHolesPaint = Paint()
        mHolesPaint.color = Color.TRANSPARENT
        mHolesPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val mHoles = ArrayList<Hole>()

    fun setHoles(holes: List<Hole>) {
        mHolesPath = Path()

        val animatorList = arrayListOf<Animator>()

        holes.forEachIndexed { index, hole ->
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

            val toRect = Rect(
                    hole.x,
                    hole.y,
                    hole.width + hole.x,
                    hole.height + hole.y)

            if (mHoles.isNotEmpty() && animation != null) {
                val fromHole = if (index < mHoles.size) mHoles[index] else null
                val fromRect = if (fromHole != null) Rect(
                        fromHole.x,
                        fromHole.y,
                        fromHole.width + fromHole.x,
                        fromHole.height + fromHole.y) else null
                if (fromRect != null) {
                    hole.rect = fromRect

                    val holeAnimator: ObjectAnimator = ObjectAnimator.ofObject(hole, "rect",
                            sRectEvaluator, fromRect, toRect)
                    holeAnimator.interpolator = getAnimationInterpolator(animation!!.timingFunction)
                    holeAnimator.addUpdateListener {
                        val value = it.animatedValue
                        value as Rect
                        if (index == 0) {
                            mHolesPath = Path()
                        }
                        mHolesPath!!.addRoundRect(
                                value.left.toFloat(),
                                value.top.toFloat(),
                                value.right.toFloat(),
                                value.bottom.toFloat(),
                                radii,
                                Path.Direction.CW
                        )
                        postInvalidate()
                    }
                    animatorList.add(holeAnimator)
                } else {
                    mHolesPath!!.addRoundRect(toRect.left.toFloat(), toRect.top.toFloat(), toRect.right.toFloat(), toRect.bottom.toFloat(),
                            radii,
                            Path.Direction.CW
                    )
                    postInvalidate()
                }
            } else {
                mHolesPath!!.addRoundRect(RectF(
                        hole.x.toFloat(),
                        hole.y.toFloat(),
                        hole.width.toFloat() + hole.x.toFloat(),
                        hole.height.toFloat() + hole.y.toFloat()),
                        radii,
                        Path.Direction.CW
                )
                postInvalidate()
            }
        }

        if (animatorList.isNotEmpty()) {
            val animatorSet = AnimatorSet()
            animatorSet.duration = animation!!.duration
            animatorSet.playTogether(animatorList)
            animatorSet.addListener(object: Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationFinished?.invoke()
                }

                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }

            })
            animatorSet.start()
        }

        mHoles.clear()
        mHoles.addAll(holes)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mHolesPath != null) {
            canvas?.drawPath(mHolesPath!!, mHolesPaint)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (mHolesPath != null) {
            canvas?.drawPath(mHolesPath!!, mHolesPaint)
        }
    }

    private fun isTouchInsideHole(touchX: Int, touchY: Int): Boolean {
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
                    val mmEventDispatcher = UIManagerHelper.getEventDispatcherForReactTag((context as ReactContext), child.id) ;
                    mmEventDispatcher!!.dispatchEvent(
                            TouchEvent.obtain(
                                    UIManagerHelper.getSurfaceId(child),
                                    child.id,
                                    TouchEventType.START,
                                    ev,
                                    ev.eventTime,
                                    ev.x,
                                    ev.y,
                                    TouchEventCoalescingKeyHelper()
                            )
                    )
                } catch (e: Exception) {
                }
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
