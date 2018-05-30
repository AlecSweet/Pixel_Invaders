package com.example.sweet.game20;

import android.app.Activity;
import android.app.ActivityManager;
import android.opengl.EGLDisplay;
import android.opengl.GLSurfaceView;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.os.Build;
import android.graphics.Point;
import android.view.Display;

import static android.opengl.EGL14.eglGetDisplay;
import static android.opengl.EGL14.eglSwapInterval;
import static android.opengl.EGL14.EGL_DEFAULT_DISPLAY;
import static android.opengl.EGL14.eglInitialize;


public class GameWatcher extends Activity{

    private GamePlayer gamePlayer;
    private GLSurfaceView glSurfaceView;
    private boolean isRendererSet = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize surface view.
        glSurfaceView = new GLSurfaceView(this);

        //Determine if the device is openGL ES 2.0+ compatible
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();

        //Actual compatibility check, detects emulators
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        //Finish GLSurfaceView initialization or exit based on compatibility

        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2);

            //glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
           // glSurfaceView.setRenderer(new GameRenderer());
            //glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            isRendererSet = true;
        } else {
            //Action for non compatible devices, Text then Exit?
            return;
        }

        //
        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        if(isRendererSet) {
            GamePlayer gamePlayer = new GamePlayer(this, size, glSurfaceView);

            // view of the Activity
            setContentView(glSurfaceView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isRendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isRendererSet) {
            glSurfaceView.onResume();
        }
    }
}
