package com.ibitcy.react_native_hole_view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.RectEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.core.graphics.withSave
import androidx.core.view.isVisible
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.TouchTargetHelper
import com.facebook.react.uimanager.UIManagerHelper
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
        var rect: Rect? = null,
    )

    class Animation(
        var duration: Long = ANIMATION_DURATION_DEFAULT,
        var timingFunction: EAnimationTimingFunction,
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

    private val mTouchCoalescingHelper = TouchEventCoalescingKeyHelper()
    private val mActiveTouches = mutableSetOf<Long>()

    init {
        this.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    private val mHoles = ArrayList<Hole>()

    fun setHoles(holes: List<Hole>) {
        if (holes.isEmpty()) {
            clearHoles()
            return
        }

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
                hole.height + hole.y
            )

            if (mHoles.isNotEmpty() && animation != null) {
                val fromHole = if (index < mHoles.size) mHoles[index] else null
                val fromRect = if (fromHole != null) Rect(
                    fromHole.x,
                    fromHole.y,
                    fromHole.width + fromHole.x,
                    fromHole.height + fromHole.y
                ) else null
                if (fromRect != null) {
                    hole.rect = fromRect

                    val holeAnimator: ObjectAnimator = ObjectAnimator.ofObject(
                        hole, "rect",
                        sRectEvaluator, fromRect, toRect
                    )
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
                    mHolesPath!!.addRoundRect(
                        toRect.left.toFloat(),
                        toRect.top.toFloat(),
                        toRect.right.toFloat(),
                        toRect.bottom.toFloat(),
                        radii,
                        Path.Direction.CW
                    )
                    postInvalidate()
                }
            } else {
                mHolesPath!!.addRoundRect(
                    RectF(
                        hole.x.toFloat(),
                        hole.y.toFloat(),
                        hole.width.toFloat() + hole.x.toFloat(),
                        hole.height.toFloat() + hole.y.toFloat()
                    ),
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
            animatorSet.addListener(object : Animator.AnimatorListener {
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

    fun clearHoles() {
        if (mHolesPath == null && mHoles.isEmpty()) {
            return
        }
        mHolesPath = null
        mHoles.clear()
        postInvalidateOnAnimation()
    }

    override fun draw(canvas: Canvas) {
        if (!hasActiveHoles()) {
            super.draw(canvas)
            return
        }

        canvas.withSave {
            clipOutHoles(canvas)
            super.draw(canvas)
        }
    }

    private fun hasActiveHoles(): Boolean {
        val path = mHolesPath
        return path != null && !path.isEmpty
    }

    private fun clipOutHoles(canvas: Canvas) {
        val holesPath = mHolesPath ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutPath(holesPath)
        } else {
            @Suppress("DEPRECATION")
            canvas.clipPath(holesPath, Region.Op.DIFFERENCE)
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val inside = isTouchInsideHole(ev.x.toInt(), ev.y.toInt())

        if (inside) {
            return passThoughToViewsUnder(ev)
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun passThoughToViewsUnder(ev: MotionEvent): Boolean {
        val parent = parent as ViewGroup
        val childrenCount = parent.childCount
        var handled = false

        for (i in childrenCount - 1 downTo 0) {
            val child = parent.getChildAt(i)

            if (child === this) {
                continue
            }

            if (child.isVisible && isViewInsideTouch(ev, child)) {
                val targetView = findTargetView(child, ev.rawX.toInt(), ev.rawY.toInt())

                if (targetView != null && targetView.id > 0) {
                    dispatchReactTouchEvent(ev, targetView)
                }

                val siblingEvent = MotionEvent.obtain(ev)
                val thisXY = IntArray(2)
                val childXY = IntArray(2)
                this.getLocationOnScreen(thisXY)
                child.getLocationOnScreen(childXY)

                val offsetX = (thisXY[0] - childXY[0]).toFloat()
                val offsetY = (thisXY[1] - childXY[1]).toFloat()
                siblingEvent.offsetLocation(offsetX, offsetY)

                if (child.dispatchTouchEvent(siblingEvent)) {
                    handled = true
                }

                siblingEvent.recycle()
            }
        }

        return handled
    }

    private fun dispatchReactTouchEvent(ev: MotionEvent, targetView: View) {
        val downTime = ev.downTime

        runCatching {
            val touchEventType = when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    if (!mActiveTouches.contains(downTime)) {
                        mActiveTouches.add(downTime)
                        mTouchCoalescingHelper.addCoalescingKey(downTime)
                    }
                    TouchEventType.START
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> TouchEventType.END
                MotionEvent.ACTION_MOVE -> TouchEventType.MOVE
                MotionEvent.ACTION_CANCEL -> TouchEventType.CANCEL
                else -> return
            }

            if (!mActiveTouches.contains(downTime)) {
                return
            }

            val transformedEvent = MotionEvent.obtain(ev)
            val thisXY = IntArray(2)
            val targetXY = IntArray(2)
            this.getLocationOnScreen(thisXY)
            targetView.getLocationOnScreen(targetXY)

            val offsetX = (thisXY[0] - targetXY[0]).toFloat()
            val offsetY = (thisXY[1] - targetXY[1]).toFloat()
            transformedEvent.offsetLocation(offsetX, offsetY)

            val eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(
                (context as ReactContext),
                targetView.id
            )
            eventDispatcher?.dispatchEvent(
                TouchEvent.obtain(
                    UIManagerHelper.getSurfaceId(targetView),
                    targetView.id,
                    touchEventType,
                    transformedEvent,
                    transformedEvent.eventTime,
                    transformedEvent.x,
                    transformedEvent.y,
                    mTouchCoalescingHelper
                )
            )

            transformedEvent.recycle()

            if (ev.actionMasked == MotionEvent.ACTION_UP ||
                ev.actionMasked == MotionEvent.ACTION_POINTER_UP ||
                ev.actionMasked == MotionEvent.ACTION_CANCEL
            ) {
                mTouchCoalescingHelper.removeCoalescingKey(downTime)
                mActiveTouches.remove(downTime)
            }
        }
    }

    private fun findTargetView(view: View, rawX: Int, rawY: Int): View? {
        if (view !is ViewGroup) {
            return if (view.id > 0) view else null
        }

        val viewXY = IntArray(2)
        view.getLocationOnScreen(viewXY)
        val localX = (rawX - viewXY[0]).toFloat()
        val localY = (rawY - viewXY[1]).toFloat()

        val viewCoords = FloatArray(2)
        val targetPath = TouchTargetHelper.findTargetPathAndCoordinatesForTouch(
            localX,
            localY,
            view,
            viewCoords
        )

        if (targetPath.isNotEmpty()) {
            for (viewTarget in targetPath) {
                val targetView = viewTarget.getView()
                if (targetView != null && targetView.id > 0) {
                    return targetView
                }
            }

            val targetId = targetPath[0].getViewId()
            if (targetId > 0) {
                return findViewWithReactTag(view, targetId)
            }
        }

        return null
    }

    private fun findViewWithReactTag(root: View, targetId: Int): View? {
        if (root.id == targetId) {
            return root
        }

        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                val found = findViewWithReactTag(root.getChildAt(i), targetId)
                if (found != null) {
                    return found
                }
            }
        }

        return null
    }

    private fun isViewInsideTouch(event: MotionEvent, view: View): Boolean {
        val viewRegion = Region()
        val xy = IntArray(2)
        view.getLocationOnScreen(xy)
        val x = xy[0]
        val y = xy[1]
        val rect = Rect(x, y, x + view.width, y + view.height)
        viewRegion.set(rect)
        return viewRegion.contains(event.rawX.toInt(), event.rawY.toInt())
    }
}
