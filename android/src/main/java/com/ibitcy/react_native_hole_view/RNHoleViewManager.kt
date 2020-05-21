package com.ibitcy.react_native_hole_view

import android.content.res.Resources
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import kotlin.math.roundToInt

class RNHoleViewManager(val reactContext: ReactApplicationContext): ViewGroupManager<RNHoleView>() {

    override fun getName(): String {
        return "RNHoleView"
    }

    override fun createViewInstance(reactContext: ThemedReactContext): RNHoleView {
        return RNHoleView(reactContext)
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
            val borderRadius = try {
                hole.getInt("borderRadius").dpToPx()
            } catch(e: Exception) {
                0
            }
            holes.add(RNHoleView.Hole(x, y, width, height, borderRadius))
        }
        view.setHoles(holes)
    }

    private fun Int.dpToPx(): Int {
        val metrics = Resources.getSystem().displayMetrics
        val px = this * (metrics.densityDpi / 160f)
        return px.roundToInt()
    }
}