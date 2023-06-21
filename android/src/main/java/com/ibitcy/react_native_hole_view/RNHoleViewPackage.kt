package com.ibitcy.react_native_hole_view

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class RNHoleViewPackage: ReactPackage {

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> =
        listOf(RNHoleViewManager(reactContext))

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> =
        emptyList()

// RNHoleViewPackage.kt: (11, 16): Type mismatch: inferred type is List<???> but MutableList<NativeModule> was expected
    // override fun createNativeModules(reactContext: ReactApplicationContext): MutableList<NativeModule> {
    //     return emptyList()
    // }

    // override fun createViewManagers(reactContext: ReactApplicationContext): MutableList<ViewManager<*, *>> {
    //     return mutableListOf(RNHoleViewManager(reactContext))
    // }

}