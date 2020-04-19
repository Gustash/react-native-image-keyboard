package com.reactlibrary;

import android.content.ClipDescription;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.textinput.ReactEditText;
import com.facebook.react.views.textinput.ReactTextInputManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ReactMediaInputManager extends ReactTextInputManager {
    private ReactImageInputWatcher mImageInputWatcher;

    ReactMediaInputManager(ReactApplicationContext reactContext) {
        super();
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        final Map<String, Object> directEventTypes = super.getExportedCustomDirectEventTypeConstants();

        if (directEventTypes == null) {
            return null;
        }

        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();

        for (Map.Entry<String, Object> event : directEventTypes.entrySet()) {
            builder.put(event.getKey(), event.getValue());
        }

        builder.put(TextInputImageEvent.EVENT_NAME, MapBuilder.of("registrationName", "onImageChange"));

        return builder.build();
    }

    private void setImageInputWatcher(ReactImageInputWatcher watcher) {
        mImageInputWatcher = watcher;
    }

    private static String loadFile(Context context, Uri contentUri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(contentUri);
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        output64.close();

        return output.toString();
    }

    @ReactProp(name = "onImageChange")
    public void setOnImageChange(final ReactEditText view, boolean onImageInput) {
        if (onImageInput) {
            setImageInputWatcher(new ReactImageInputWatcher(view));
        } else {
            setImageInputWatcher(null);
        }
    }

    private static class ReactImageInputWatcher implements ImageInputWatcher {

        private ReactEditText mReactEditText;
        private EventDispatcher mEventDispatcher;

        ReactImageInputWatcher(ReactEditText editText) {
            mReactEditText = editText;
            ReactContext reactContext = (ReactContext) editText.getContext();
            mEventDispatcher = reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();
        }

        @Override
        public void onImageInput(String uri, String linkUri, String data, String mime) {
            if (uri != null) {
                TextInputImageEvent event = new TextInputImageEvent(
                        mReactEditText.getId(),
                        uri,
                        linkUri,
                        data,
                        mime);


                mEventDispatcher.dispatchEvent(event);
            }
        }
    }

    @NonNull
    @Override
    public ReactEditText createViewInstance(@NonNull final ThemedReactContext reactContext) {
        return new ReactEditText(reactContext) {
            @Override
            public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
                InputConnection ic = super.onCreateInputConnection(outAttrs);

                EditorInfoCompat.setContentMimeTypes(outAttrs, new String [] {
                        "image/png",
                        "image/gif",
                        "image/jpg",
                        "image/jpeg",
                        "image/webp"
                });

                final InputConnectionCompat.OnCommitContentListener callback =
                        new InputConnectionCompat.OnCommitContentListener() {
                            @Override
                            public boolean onCommitContent(InputContentInfoCompat inputContentInfo,
                                                           int flags, Bundle opts) {
                                // read and display inputContentInfo asynchronously
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && (flags &
                                        InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                                    try {
                                        inputContentInfo.requestPermission();
                                    }
                                    catch (Exception e) {
                                        return false; // return false if failed
                                    }
                                }

                                // Avoid loading the image into memory if its not going to be used
                                if (mImageInputWatcher == null) {
                                    return false;
                                }

                                String uri;
                                String linkUri = null;
                                String mime = null;
                                String data;

                                Uri contentUri = inputContentInfo.getContentUri();
                                uri = contentUri.toString();

                                // Load the data, we have to do this now otherwise we cannot release permissions
                                try {
                                    data = loadFile(reactContext, contentUri);
                                }
                                catch(IOException e) {
                                    inputContentInfo.releasePermission();
                                    return false;
                                }

                                // Get the optional uri to web link
                                Uri link = inputContentInfo.getLinkUri();
                                if (link != null) {
                                    linkUri = link.toString();
                                }

                                ClipDescription description = inputContentInfo.getDescription();
                                if (description.getMimeTypeCount() > 0) {
                                    mime = description.getMimeType(0);
                                }

                                mImageInputWatcher.onImageInput(uri, linkUri, data, mime);

                                // Releasing the permission means that the content at the uri is probably no longer accessible
                                inputContentInfo.releasePermission();

                                return true;
                            }
                        };

                return InputConnectionCompat.createWrapper(ic, outAttrs, callback);
            }
        };
    }
}
