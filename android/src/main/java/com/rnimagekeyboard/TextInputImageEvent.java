/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.rnimagekeyboard;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class TextInputImageEvent extends Event<TextInputImageEvent> {
    static final String EVENT_NAME = "topImageChangeEvent";

    private String mUri;
    private String mLinkUri;
    private String mData;
    private String mMime;

    TextInputImageEvent(
            int viewId,
            String uri,
            String linkUri,
            String data,
            String mime) {
        super(viewId);
        mUri = uri;
        mLinkUri = linkUri;
        mData = data;
        mMime = mime;
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
    }

    private WritableMap serializeEventData() {
        WritableMap eventData = Arguments.createMap();
        eventData.putString("uri", mUri);
        eventData.putString("linkUri", mLinkUri);
        eventData.putString("data", mData);
        eventData.putString("mime", mMime);
        eventData.putInt("target", getViewTag());
        return eventData;
    }
}
