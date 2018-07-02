package com.googlecast;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.bridge.ReadableArray;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;

import java.util.Map;

import javax.annotation.Nullable;


public class GoogleCastButtonManager extends SimpleViewManager<MediaRouteButton> {

    public static final String REACT_CLASS = "RNGoogleCastButton";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public MediaRouteButton createViewInstance(ThemedReactContext context) {
        CastContext castContext = CastContext.getSharedInstance(context);

        final MediaRouteButton button = new ColorableMediaRouteButton(context);
        CastButtonFactory.setUpMediaRouteButton(context, button);

        updateButtonState(button, castContext.getCastState());

        castContext.addCastStateListener(new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                GoogleCastButtonManager.this.updateButtonState(button, newState);
            }
        });

        return button;
    }

    @ReactProp(name = "tintColor", customType = "Color")
    public void setTintColor(ColorableMediaRouteButton button, Integer color) {
        if (color == null) return;
        button.applyTint(color);
    }

    private void updateButtonState(MediaRouteButton button, int state) {
        // hide the button when no device available (default behavior is show it disabled)
        if (CastState.NO_DEVICES_AVAILABLE == state) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of("click", 1);
    }

    @Override
    public void receiveCommand(MediaRouteButton view, int commandId, @Nullable ReadableArray args) {
        if (commandId == 1) view.performClick();
    }

    // https://stackoverflow.com/a/41496796/384349
    private class ColorableMediaRouteButton extends MediaRouteButton {
        protected Drawable mRemoteIndicatorDrawable;

        public ColorableMediaRouteButton(Context context) {
            super(context);
        }

        public ColorableMediaRouteButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ColorableMediaRouteButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public void setRemoteIndicatorDrawable(Drawable d) {
            mRemoteIndicatorDrawable = d;
            super.setRemoteIndicatorDrawable(d);
        }

        public void applyTint(Integer color) {
            ContextThemeWrapper ctw = new ContextThemeWrapper(super.getContext(), android.support.v7.mediarouter.R.style.Theme_MediaRouter);
            TypedArray a = ctw.obtainStyledAttributes(null,
                android.support.v7.mediarouter.R.styleable.MediaRouteButton, android.support.v7.mediarouter.R.attr.mediaRouteButtonStyle, 0);
            Drawable drawable = a.getDrawable(
                android.support.v7.mediarouter.R.styleable.MediaRouteButton_externalRouteEnabledDrawable);
            a.recycle();
            DrawableCompat.setTint(drawable, color);
            super.setRemoteIndicatorDrawable(drawable);
        }
    }
}
