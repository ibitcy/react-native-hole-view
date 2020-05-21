package com.ibitcy.react_native_hole_view

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp

class RNHoleViewManager(val reactContext: ReactApplicationContext): ViewGroupManager<RNHoleView>() {

    override fun getName(): String {
        return "RNHoleView"
    }

    override fun createViewInstance(reactContext: ThemedReactContext): RNHoleView {
        return RNHoleView(reactContext)
    }

    @ReactProp(name = "holes")
    fun setHoles(view: RNHoleView, holes: ReadableArray) {
        if (holes.size() == 0) {
            return
        }

        for (i in 0 until holes.size()) {
            val hole = holes.getMap(i)!!
            val x = hole.getInt("x")
            val y = hole.getInt("y")
            val width = hole.getInt("width")
            val height = hole.getInt("height")
            val borderRadius = try {
                hole.getInt("borderRadius")
            } catch(e: Exception) {
                0
            }
            view.addHole(RNHoleView.Hole(x, y, width, height, borderRadius))
        }

    }
}