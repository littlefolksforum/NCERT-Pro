package com.urexamhelp.ncertpro;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.urexamhelp.ncertpro.BuildConfig;

public class TopprWebView extends WebView {

    public TopprWebView(Context context) {
        super(context);
        if (!isInEditMode()) {
            setWebDebug();
        }
    }

    public TopprWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            setWebDebug();
        }
    }

    public TopprWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            setWebDebug();
        }
    }

    public TopprWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            setWebDebug();
        }
    }

    public TopprWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        if (!isInEditMode()) {
            setWebDebug();
        }
    }

    public static void setWebDebug() {
        if (BuildConfig.DEBUG) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                WebView.setWebContentsDebuggingEnabled(true);
        }
    }
}
