package com.ibitcy.react_native_hole_view

import android.content.res.Resources

import com.facebook.react.bridge.*
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.facebook.react.uimanager.ViewManagerDelegate

import com.ibitcy.react_native_hole_view.events.AnimFinishEvent

import kotlin.math.roundToInt

class RNHoleViewManagerImpl(reactContext: ReactApplicationContext) {
    
    val reactContext: ReactApplicationContext = reactContext

    companion object {
        public const val NAME = "RNHoleView"
        public const val ON_ANIMATION_FINISHED = "onAnimationFinished"
    }

    public fun setAnimation(view: RNHoleView, animation: ReadableMap?) {
        if (animation != null) {
            var duration = RNHoleView.ANIMATION_DURATION_DEFAULT
            if (animation.hasKey("duration")) {
                duration = animation.getDouble("duration").toLong()
            }
            var timingFunction: RNHoleView.EAnimationTimingFunction? = null
            if (animation.hasKey("timingFunction")) {
                timingFunction =
                    RNHoleView.EAnimationTimingFunction.valueOf(animation.getString("timingFunction")!!)
            }

            if (timingFunction != null) {
                view.animation = RNHoleView.Animation(duration, timingFunction)
            }
        }
    }

    public fun setHoles(view: RNHoleView, holesArg: ReadableArray?) {
        if (holesArg == null || holesArg.size() == 0) {
            return
        }

        val holes = mutableListOf<RNHoleView.Hole>()
        for (i in 0 until holesArg.size()) {
            val hole = holesArg.getMap(i)!!
            val x = hole.getInt("x").dpToPx()
            val y = hole.getInt("y").dpToPx()
            val width = hole.getInt("width").dpToPx()
            val height = hole.getInt("height").dpToPx()

            val isRTL = try {
                hole.getBoolean("isRTL")
            } catch (e: Exception) {
                false
            }

            val borderRadius = try {
                hole.getInt("borderRadius").dpToPx()
            } catch(e: Exception) {
                0
            }

            val borderTopLeftRadius = try {
                val value = hole.getInt("borderTopLeftRadius")
                if (value == -1) borderRadius else value.dpToPx()
            } catch(e: Exception) {
                borderRadius
            }

            val borderTopRightRadius = try {
                val value = hole.getInt("borderTopRightRadius")
                if (value == -1) borderRadius else value.dpToPx()
            } catch(e: Exception) {
                borderRadius
            }

            val borderBottomLeftRadius = try {
                val value = hole.getInt("borderBottomLeftRadius")
                if (value == -1) borderRadius else value.dpToPx()
            } catch(e: Exception) {
                borderRadius
            }

            val borderBottomRightRadius = try {
                val value = hole.getInt("borderBottomRightRadius")
                if (value == -1) borderRadius else value.dpToPx()
            } catch(e: Exception) {
                borderRadius
            }

            val borderBottomStartRadius = try {
                val value = hole.getInt("borderBottomStartRadius")
                if (value == -1) borderRadius else value.dpToPx()
            } catch (e: Exception) {
                if (isRTL) borderBottomRightRadius else borderBottomLeftRadius
            }

            val borderBottomEndRadius = try {
                val value = hole.getInt("borderBottomEndRadius")
                if (value == -1) borderRadius else value.dpToPx()
            } catch (e: Exception) {
                if (isRTL) borderBottomLeftRadius else borderBottomRightRadius
            }

            val borderTopStartRadius = try {
                val value = hole.getInt("borderTopStartRadius")
                if (value == -1) borderRadius else value.dpToPx()
            } catch (e: Exception) {
                if (isRTL) borderTopRightRadius else borderTopLeftRadius
            }

            val borderTopEndRadius = try {
                val value = hole.getInt("borderTopEndRadius")
                if (value == -1) borderRadius else value.dpToPx()
            } catch (e: Exception) {
                if (isRTL) borderTopLeftRadius else borderTopRightRadius
            }

            if (isRTL) {
                holes.add(RNHoleView.Hole(
                        x, y, width, height,
                        borderTopEndRadius,
                        borderTopStartRadius,
                        borderBottomEndRadius,
                        borderBottomStartRadius)
                )
            } else {
                holes.add(RNHoleView.Hole(
                        x, y, width, height,
                        borderTopStartRadius,
                        borderTopEndRadius,
                        borderBottomStartRadius,
                        borderBottomEndRadius)
                )
            }
        }
        view.setHoles(holes)
    }

    private fun Int.dpToPx(): Int {
        val metrics = Resources.getSystem().displayMetrics
        val px = this * (metrics.densityDpi / 160f)
        return px.roundToInt()
    }
}
