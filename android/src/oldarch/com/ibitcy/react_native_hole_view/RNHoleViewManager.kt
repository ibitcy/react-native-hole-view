package com.ibitcy.react_native_hole_view

import android.content.res.Resources

import com.facebook.react.bridge.*
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter

class RNHoleViewManager(private val reactContext: ReactApplicationContext): ViewGroupManager<RNHoleView>() {

    private val impl = RNHoleViewManagerImpl(reactContext)

    override fun getName() = RNHoleViewManagerImpl.NAME

    override fun createViewInstance(reactContext: ThemedReactContext): RNHoleView {
        val v = RNHoleView(reactContext)
        v.onAnimationFinished = {
            reactContext.getJSModule(RCTEventEmitter::class.java).receiveEvent(
                v.id,
                RNHoleViewManagerImpl.ON_ANIMATION_FINISHED,
                Arguments.createMap()
            )
        }
        return v
    }

    override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any> {
        return MapBuilder.builder<String, Any>()
                .put(RNHoleViewManagerImpl.ON_ANIMATION_FINISHED, MapBuilder.of(
                        "registrationName", RNHoleViewManagerImpl.ON_ANIMATION_FINISHED
                    )
                )
                .build()
    }

    @ReactProp(name = "animation")
    fun setAnimation(view: RNHoleView, animation: ReadableMap?) {
        impl.setAnimation(view, animation)
    }

    @ReactProp(name = "holes")
    fun setHoles(view: RNHoleView, holesArg: ReadableArray?) {
        impl.setHoles(view, holesArg)
    }
}