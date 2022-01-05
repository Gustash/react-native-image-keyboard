package com.rnimagekeyboard

interface ImageInputWatcher {
    fun onImageInput(uri: String?, linkUri: String?, data: String?, mime: String?)
}