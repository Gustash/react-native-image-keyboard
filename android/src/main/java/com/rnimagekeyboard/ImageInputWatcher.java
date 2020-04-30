package com.rnimagekeyboard;

public interface ImageInputWatcher {
    void onImageInput(String uri, String linkUri, String data, String mime);
}
