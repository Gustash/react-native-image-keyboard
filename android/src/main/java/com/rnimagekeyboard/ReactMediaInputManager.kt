package com.rnimagekeyboard

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputConnectionCompat.OnCommitContentListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerModule
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.EventDispatcher
import com.facebook.react.views.textinput.ReactEditText
import com.facebook.react.views.textinput.ReactTextInputManager
import org.apache.commons.io.IOUtils
import java.io.*

class ReactMediaInputManager internal constructor(reactContext: ReactApplicationContext?) : ReactTextInputManager() {
    private var mImageInputWatcher: ReactImageInputWatcher? = null

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any>? {
        val directEventTypes = super.getExportedCustomDirectEventTypeConstants() ?: return null
        val builder = MapBuilder.builder<String, Any>()
        for ((key, value) in directEventTypes) {
            builder.put(key, value)
        }
        builder.put(TextInputImageEvent.EVENT, MapBuilder.of("registrationName", "onImageChange"))
        return builder.build()
    }

    private fun setImageInputWatcher(watcher: ReactImageInputWatcher?) {
        mImageInputWatcher = watcher
    }

    @ReactProp(name = "onImageChange")
    fun setOnImageChange(view: ReactEditText, onImageInput: Boolean) {
        if (onImageInput) {
            setImageInputWatcher(ReactImageInputWatcher(view))
        } else {
            setImageInputWatcher(null)
        }
    }

    private class ReactImageInputWatcher(private val mReactEditText: ReactEditText) : ImageInputWatcher {
        private val mEventDispatcher: EventDispatcher?
        override fun onImageInput(uri: String?, linkUri: String?, data: String?, mime: String?) {
            if (uri != null) {
                val event = TextInputImageEvent(
                        mReactEditText.id,
                        uri,
                        linkUri,
                        data,
                        mime)
                mEventDispatcher?.dispatchEvent(event)
            }
        }

        init {
            val reactContext = mReactEditText.context as ReactContext
            mEventDispatcher = reactContext.getNativeModule(UIManagerModule::class.java)?.eventDispatcher
        }
    }

    override fun createViewInstance(reactContext: ThemedReactContext): ReactEditText {
        return object : ReactEditText(reactContext) {
            override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
                val ic = super.onCreateInputConnection(outAttrs)
                EditorInfoCompat.setContentMimeTypes(outAttrs, arrayOf(
                        "image/png",
                        "image/gif",
                        "image/jpg",
                        "image/jpeg",
                        "image/webp"
                ))
                val callback = OnCommitContentListener { inputContentInfo, flags, opts ->
                    // read and display inputContentInfo asynchronously
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 &&
                            flags and InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION != 0) {
                        try {
                            inputContentInfo.requestPermission()
                        } catch (e: Exception) {
                            return@OnCommitContentListener false // return false if failed
                        }
                    }

                    // Avoid loading the image into memory if its not going to be used
                    if (mImageInputWatcher == null) {
                        return@OnCommitContentListener false
                    }
                    var uri: String
                    var linkUri: String? = null
                    var mime: String? = null
                    val data: String
                    val contentUri = inputContentInfo.contentUri
                    uri = contentUri.toString()

                    // Load the data, we have to do this now otherwise we cannot release permissions
                    try {
                        uri = saveFile(reactContext, contentUri).toString()
                        data = loadFile(reactContext, contentUri)
                    } catch (e: IOException) {
                        inputContentInfo.releasePermission()
                        return@OnCommitContentListener false
                    }

                    // Get the optional uri to web link
                    val link = inputContentInfo.linkUri
                    if (link != null) {
                        linkUri = link.toString()
                    }
                    val description = inputContentInfo.description
                    if (description.mimeTypeCount > 0) {
                        mime = description.getMimeType(0)
                    }
                    mImageInputWatcher!!.onImageInput(uri, linkUri, data, mime)

                    // Releasing the permission means that the content at the uri is probably no longer accessible
                    inputContentInfo.releasePermission()
                    true
                }

                // Only wrap non-null input connections otherwise a crash will occur
                return if (ic != null) InputConnectionCompat.createWrapper(ic, outAttrs, callback) else ic!!
            }
        }
    }

    companion object {
        @Throws(IOException::class)
        private fun loadFile(context: Context, contentUri: Uri): String {
            val inputStream = context.contentResolver.openInputStream(contentUri)
            val output = ByteArrayOutputStream()
            val output64 = Base64OutputStream(output, Base64.DEFAULT)
            try {
                IOUtils.copy(inputStream, output64)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            output64.close()
            return output.toString()
        }

        @Throws(IOException::class)
        private fun saveFile(context: Context, contentUri: Uri): Uri {
            val cacheDir = File(context.cacheDir, "MediaInputCache")
            cacheDir.mkdir() //Create if not existent
            val target = File(cacheDir, contentUri.lastPathSegment!!)
            val inputStream = context.contentResolver.openInputStream(contentUri)!!
            val outputStream = FileOutputStream(target)
            IOUtils.copy(inputStream, outputStream)
            return Uri.fromFile(target)
        }
    }
}