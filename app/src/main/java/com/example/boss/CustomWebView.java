package com.example.boss;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ScrollView;

public class CustomWebView extends WebView {
    private boolean mIsMapTouched = false;

    public CustomWebView(Context context) {
        super(context);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        // Get WebSettings
        WebSettings webSettings = getSettings();

        // Get the current user agent string
        String currentUserAgent = webSettings.getUserAgentString();

        // Append the preferred language to the user agent string
        String newUA = currentUserAgent + " en"; // Append " en" for English

        // Set the new user agent string
        webSettings.setUserAgentString(newUA);

        // Other WebView settings...
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // User started interacting with the map
                mIsMapTouched = true;
                getParentScrollView().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // User stopped interacting with the map
                mIsMapTouched = false;
                getParentScrollView().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onTouchEvent(event);
    }

    private ScrollView getParentScrollView() {
        ViewParent parent = getParent();
        while (parent != null && !(parent instanceof ScrollView)) {
            parent = parent.getParent();
        }
        return (ScrollView) parent;
    }
}