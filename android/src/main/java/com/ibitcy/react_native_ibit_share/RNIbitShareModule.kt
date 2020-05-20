package com.ibitcy.react_native_ibit_share

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.bytedance.sdk.open.aweme.base.MediaContent
import com.bytedance.sdk.open.aweme.base.VideoObject
import com.bytedance.sdk.open.aweme.share.Share
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.react.bridge.*
import com.facebook.share.Sharer
import com.facebook.share.model.ShareVideo
import com.facebook.share.model.ShareVideoContent
import com.facebook.share.widget.ShareDialog
import com.ibitcy.react_native_ibit_share.utils.JsonUtils.convertJsonToMap
import com.ibitcy.react_native_ibit_share.utils.JsonUtils.toJsonString
import org.json.JSONObject
import java.io.File


internal class RNIbitShareModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val OPTION_INSTAGRAM = "instagram"
        private const val PACKAGE_INSTAGRAM_1 = "com.instagram.android"
        private const val PACKAGE_INSTAGRAM_2 = "com.instagram.lite"
        private val PACKAGES_INSTAGRAM = arrayListOf(PACKAGE_INSTAGRAM_1, PACKAGE_INSTAGRAM_2)

        private const val OPTION_TIKTOK = "tiktok"
        private const val PACKAGE_TIKTOK_1 = "com.zhiliaoapp.musically"
        private const val PACKAGE_TIKTOK_2 = "com.zhiliaoapp.musically.go"
        private const val PACKAGE_TIKTOK_3 = "com.ss.android.ugc.trill.go"
        private const val PACKAGE_TIKTOK_CHINA = "com.ss.android.ugc.aweme"
        private val PACKAGES_TIKTOK = arrayListOf(PACKAGE_TIKTOK_1, PACKAGE_TIKTOK_2, PACKAGE_TIKTOK_3, PACKAGE_TIKTOK_CHINA)

        private const val OPTION_FACEBOOK = "facebook"
        private const val PACKAGE_FACEBOOK_1 = "com.facebook.katana"
        private const val PACKAGE_FACEBOOK_2 = "com.facebook.lite"
        private val PACKAGES_FACEBOOK = arrayListOf(PACKAGE_FACEBOOK_1, PACKAGE_FACEBOOK_2)

        private const val OPTION_TWITTER = "twitter"
        private const val PACKAGE_TWITTER_1 = "com.twitter.android"
        private const val PACKAGE_TWITTER_2 = "com.twitter.android.lite"
        private val PACKAGES_TWITTER = arrayListOf(PACKAGE_TWITTER_1, PACKAGE_TWITTER_2)

        private const val OPTION_YOUTUBE = "youtube"
        private const val PACKAGE_YOUTUBE_1 = "com.google.android.youtube"
        private val PACKAGES_YOUTUBE = arrayListOf(PACKAGE_YOUTUBE_1)

        private const val OPTION_SNAPCHAT = "snapchat"
        private const val PACKAGE_SNAPCHAT_1 = "com.snapchat.android"
        private val PACKAGES_SNAPCHAT = arrayListOf(PACKAGE_SNAPCHAT_1)

        private val OPTIONS = arrayListOf(OPTION_TIKTOK, OPTION_INSTAGRAM, OPTION_FACEBOOK, OPTION_TWITTER, OPTION_YOUTUBE, OPTION_SNAPCHAT)
    }

    override fun getName(): String {
        return "RNIbitShare"
    }

    class ShareOption {
        lateinit var shareOption: String
        var galleryIdRecommended = false
    }

    @ReactMethod
    fun getShareOptions(cb: Promise) {
        try {
            val available = WritableNativeArray()
            OPTIONS.forEach {
                if (checkOptionAvailability(it)) {
                    val option = ShareOption()
                    option.shareOption = it
                    val json = toJsonString(option)
                    val map = convertJsonToMap(JSONObject(json!!))
                    available.pushMap(map)
                }
            }
            cb.resolve(available)
        } catch (e: Exception) {
            cb.reject(e)
        }
    }

    @ReactMethod
    fun shareVideo(videoURL: String?, videoId: String?, shareOption: String?, description: String?, cb: Promise) {
        try {
            if (videoURL.isNullOrEmpty() || shareOption.isNullOrEmpty() || !checkOptionAvailability(shareOption)) {
                cb.resolve(null)
                return
            }
            val path = videoURL.replace("file://", "")
            share(path, shareOption, description, cb)
        } catch (e : Exception) {
            cb.reject(e)
        }
    }

    private fun checkOptionAvailability(option: String): Boolean {

        fun installed(packages: List<String>): Boolean {
            packages.forEach {
                if (isPackageInstalled(reactContext, it)) {
                    return true
                }
            }
            return false
        }

        return when(option) {
            OPTION_FACEBOOK -> installed(PACKAGES_FACEBOOK)
            OPTION_INSTAGRAM -> installed(PACKAGES_INSTAGRAM)
            OPTION_TIKTOK -> installed(PACKAGES_TIKTOK)
            OPTION_TWITTER -> installed(PACKAGES_TWITTER)
            OPTION_YOUTUBE -> installed(PACKAGES_YOUTUBE)
            OPTION_SNAPCHAT -> installed(PACKAGES_SNAPCHAT)
            else -> false
        }
    }

    private fun isPackageInstalled(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return false
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }

    private fun share(videoPath: String, shareOption: String, description: String?, cb: Promise) {
        when (shareOption) {
            OPTION_FACEBOOK -> {
                val callbackManager = CallbackManager.Factory.create()
                val shareDialog = ShareDialog(reactContext.currentActivity)
                shareDialog.registerCallback(callbackManager, object: FacebookCallback<Sharer.Result> {
                    override fun onSuccess(result: Sharer.Result?) {
                        cb.resolve(true)
                    }

                    override fun onCancel() {
                        cb.resolve(false)
                    }

                    override fun onError(error: FacebookException?) {
                        error?.printStackTrace()
                        cb.reject(error)
                    }
                })

                val video = ShareVideo.Builder()
                        .setLocalUrl(Uri.fromFile(File(videoPath)))
                        .build()
                val content = ShareVideoContent.Builder()
                        .setVideo(video)
                        .build()
                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC)
            }

            OPTION_INSTAGRAM -> {
                val share = getIntentForShare(videoPath)
                PACKAGES_INSTAGRAM.forEach {
                    if (isPackageInstalled(reactContext, it)) {
                        share.setPackage(it)
                    }
                }
                try {
                    reactContext.currentActivity!!.startActivity(Intent.createChooser(share, "Instagram"))
                    cb.resolve(true)
                } catch (e : Exception) {
                    e.printStackTrace()
                    cb.reject(e)
                }
            }

            OPTION_TIKTOK -> {
                var chinese = false
                PACKAGES_TIKTOK.forEach {
                    if (isPackageInstalled(reactContext, it)) {
                        chinese = true
                    }
                }

                if (!chinese) {
                    val tiktokOpenApi = TikTokOpenApiFactory.create(reactContext.currentActivity)
                    val request = Share.Request()
                    val uris = arrayListOf(videoPath)

                    val videoObject = VideoObject()
                    videoObject.mVideoPaths = uris
                    val content = MediaContent()
                    content.mMediaObject = videoObject
                    request.mMediaContent = content
                    request.mState = "ss"
                    tiktokOpenApi.share(request)
                } else {

                }
            }

            OPTION_TWITTER -> {
                val share = getIntentForShare(videoPath)
                PACKAGES_TWITTER.forEach {
                    if (isPackageInstalled(reactContext, it)) {
                        share.setPackage(it)
                    }
                }
                try {
                    reactContext.currentActivity!!.startActivity(Intent.createChooser(share, "Twitter"))
                    cb.resolve(true)
                } catch (e : Exception) {
                    e.printStackTrace()
                    cb.reject(e)
                }
            }

            OPTION_YOUTUBE -> {
                val share = getIntentForShare(videoPath)
                PACKAGES_YOUTUBE.forEach {
                    if (isPackageInstalled(reactContext, it)) {
                        share.setPackage(it)
                    }
                }
                try {
                    reactContext.currentActivity!!.startActivity(Intent.createChooser(share, "Youtube"))
                    cb.resolve(true)
                } catch (e : Exception) {
                    e.printStackTrace()
                    cb.reject(e)
                }
            }

            OPTION_SNAPCHAT -> {
                val share = getIntentForShare(videoPath)
                PACKAGES_SNAPCHAT.forEach {
                    if (isPackageInstalled(reactContext, it)) {
                        share.setPackage(it)
                    }
                }
                try {
                    reactContext.currentActivity!!.startActivity(Intent.createChooser(share, "Snapchat"))
                    cb.resolve(true)
                } catch (e : Exception) {
                    e.printStackTrace()
                    cb.reject(e)
                }
            }
        }
    }

    private fun getIntentForShare(videoPath: String): Intent {
        val share = Intent(Intent.ACTION_SEND)
        val file = File(videoPath)
        val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
        share.type = type
        val uri = getUriFromFile(file)

        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        share.putExtra(Intent.EXTRA_STREAM, uri)
        return share
    }

    private fun getUriFromFile(file: File): Uri {
        return FileProvider.getUriForFile(
                reactContext,
                reactContext.applicationContext.packageName + ".shareFileProvider",
                file
        )
    }
}
