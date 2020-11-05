package com.ibitcy.react_native_hole_view

import android.content.res.Resources
import com.facebook.react.bridge.*
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter
import kotlin.math.roundToInt

class RNHoleViewManager(private val reactContext: ReactApplicationContext): ViewGroupManager<RNHoleView>() {

    companion object {
        const val EVENT_BUBBLED = "bubbled"

        const val ON_ANIMATION_FINISHED = "onAnimationFinished"
    }

    override fun getName(): String {
        return "RNHoleView"
    }

    override fun createViewInstance(reactContext: ThemedReactContext): RNHoleView {
        val v = RNHoleView(reactContext)
        v.onAnimationFinished = {
            emitEvent(v, ON_ANIMATION_FINISHED, Arguments.createMap())
        }
        return v
    }

    override fun getExportedCustomBubblingEventTypeConstants(): MutableMap<String, Any> {
        return MapBuilder.builder<String, Any>()
                .put(ON_ANIMATION_FINISHED, MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of<Any, Any>(EVENT_BUBBLED, ON_ANIMATION_FINISHED)
                        )
                )
                .build()
    }

    private fun emitEvent(view: RNHoleView, name: String, arguments: WritableMap) {
        reactContext.getJSModule(RCTEventEmitter::class.java).receiveEvent(
                view.id,
                name,
                arguments
        )
    }

    @ReactProp(name = "animation")
    fun setAnimation(view: RNHoleView, animation: ReadableMap) {
        var duration = RNHoleView.ANIMATION_DURATION_DEFAULT
        if (animation.hasKey("duration")) {
            duration = animation.getDouble("duration").toLong()
        }
        var timingFunction: RNHoleView.EAnimationTimingFunction? = null
        if (animation.hasKey("timingFunction")) {
            timingFunction = RNHoleView.EAnimationTimingFunction.valueOf(animation.getString("timingFunction")!!)
        }

        if (timingFunction != null) {
            view.animation = RNHoleView.Animation(duration, timingFunction)
        }
    }

    @ReactProp(name = "holes")
    fun setHoles(view: RNHoleView, holesArg: ReadableArray) {
        if (holesArg.size() == 0) {
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
                val value = hole.getInt("borderTopLeftRadius").dpToPx()
                if (value == -1) borderRadius else value
            } catch(e: Exception) {
                borderRadius
            }

            val borderTopRightRadius = try {
                val value = hole.getInt("borderTopRightRadius").dpToPx()
                if (value == -1) borderRadius else value
            } catch(e: Exception) {
                borderRadius
            }

            val borderBottomLeftRadius = try {
                val value = hole.getInt("borderBottomLeftRadius").dpToPx()
                if (value == -1) borderRadius else value
            } catch(e: Exception) {
                borderRadius
            }

            val borderBottomRightRadius = try {
                val value = hole.getInt("borderBottomRightRadius").dpToPx()
                if (value == -1) borderRadius else value
            } catch(e: Exception) {
                borderRadius
            }

            val borderBottomStartRadius = try {
                val value = hole.getInt("borderBottomStartRadius").dpToPx()
                if (value == -1) borderRadius else value
            } catch (e: Exception) {
                if (isRTL) borderBottomRightRadius else borderBottomLeftRadius
            }

            val borderBottomEndRadius = try {
                val value = hole.getInt("borderBottomEndRadius").dpToPx()
                if (value == -1) borderRadius else value
            } catch (e: Exception) {
                if (isRTL) borderBottomLeftRadius else borderBottomRightRadius
            }

            val borderTopStartRadius = try {
                val value = hole.getInt("borderTopStartRadius").dpToPx()
                if (value == -1) borderRadius else value
            } catch (e: Exception) {
                if (isRTL) borderTopRightRadius else borderTopLeftRadius
            }

            val borderTopEndRadius = try {
                val value = hole.getInt("borderTopEndRadius").dpToPx()
                if (value == -1) borderRadius else value
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