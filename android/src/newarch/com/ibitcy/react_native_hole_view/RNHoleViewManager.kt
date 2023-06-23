package com.ibitcy.react_native_hole_view

import com.ibitcy.react_native_hole_view.events.AnimFinishEvent

import android.content.res.Resources

import com.facebook.react.bridge.*
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.events.EventDispatcher

import com.facebook.react.viewmanagers.RNHoleViewManagerInterface
import com.facebook.react.viewmanagers.RNHoleViewManagerDelegate

@ReactModule(name = RNHoleViewManagerImpl.NAME)
class RNHoleViewManager(val reactContext: ReactApplicationContext): ViewGroupManager<RNHoleView>(), RNHoleViewManagerInterface<RNHoleView> {

    val impl = RNHoleViewManagerImpl(reactContext)

    private val delegate: RNHoleViewManagerDelegate<RNHoleView,RNHoleViewManager> = RNHoleViewManagerDelegate(this)

    override fun getDelegate(): ViewManagerDelegate<RNHoleView> = delegate

    override fun getName() = RNHoleViewManagerImpl.NAME

    override fun createViewInstance(reactContext: ThemedReactContext): RNHoleView {
        val v = RNHoleView(reactContext)
        v.onAnimationFinished = {
            val event = AnimFinishEvent(v.id)
            val eventDispatcher: EventDispatcher? =
                UIManagerHelper.getEventDispatcherForReactTag(reactContext, v.id)
            eventDispatcher?.dispatchEvent(event)
        }
        return v
    }

    override fun getExportedCustomBubblingEventTypeConstants(): MutableMap<String, Any> {
        return MapBuilder.builder<String, Any>()
                .put("topAnimationFinished", MapBuilder.of(
                        "phasedRegistrationNames",
                        MapBuilder.of<Any, Any>("bubbled", RNHoleViewManagerImpl.ON_ANIMATION_FINISHED)
                    )
                )
                .build()
    }

    @ReactProp(name = "animation")
    override fun setAnimation(view: RNHoleView, animation: ReadableMap?) {
        impl.setAnimation(view,animation)
    }

    @ReactProp(name = "holes")
    override fun setHoles(view: RNHoleView, holesArg: ReadableArray?) {
        impl.setHoles(view,holesArg)
    }
}
