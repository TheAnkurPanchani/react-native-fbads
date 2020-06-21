package com.reactnativefbads;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdsManager;
import com.facebook.ads.NativeBannerAd;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NativeBannerAdViewManager extends ViewGroupManager<NativeBannerAdView> {
    private final String TAG = NativeBannerAdViewManager.class.getSimpleName();
    private NativeBannerAd nativeBannerAd;
    private NativeBannerAdView nativeBannerAdView;
    private String placementId;

    @Override
    public String getName() {
        return "NativeBannerAd";
    }

    @Override
    protected NativeBannerAdView createViewInstance(ThemedReactContext reactContext) {
        nativeBannerAdView = new NativeBannerAdView(reactContext);
        return nativeBannerAdView;
    }

    @ReactProp(name = "placementId")
    public void setPlacementId(NativeBannerAdView view, final String placementId) {
        Context viewContext = view.getContext();
        if (viewContext instanceof ReactContext) {
            ReactContext reactContext = (ReactContext) viewContext;
            final NativeBannerAdManager adManager = reactContext.getNativeModule(NativeBannerAdManager.class);
            this.placementId = placementId;
            nativeBannerAd = new NativeBannerAd(reactContext, placementId);
            nativeBannerAd.setAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                    Log.e(TAG, "Native ad finished downloading all assets.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Native ad failed to load
                    Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Native ad is loaded and ready to be displayed
                    Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                    // Race condition, load() called again before last ad was displayed
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    adManager.setNativeBannerAd(placementId, nativeBannerAd);
                    nativeBannerAdView.setNativeBannerAd(nativeBannerAd);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                    Log.d(TAG, "Native ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                    Log.d(TAG, "Native ad impression logged!");
                }
            });
            // load the ad
            nativeBannerAd.loadAd();
        } else {
            Log.e("E_NOT_RCT_CONTEXT", "View's context is not a ReactContext, so it's not possible to get NativeAdManager.");
        }
    }

    @Override
    @Nullable
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>of(
                "onAdLoaded",
                MapBuilder.of("registrationName", "onAdLoaded"),
                "onAdFailed",
                MapBuilder.of("registrationName", "onAdFailed")
        );
    }

    @Override
    public void addView(NativeBannerAdView parent, View child, int index) {
        parent.addView(child, index);
    }

    @Override
    public int getChildCount(NativeBannerAdView parent) {
        return parent.getChildCount();
    }

    @Override
    public View getChildAt(NativeBannerAdView parent, int index) {
        return parent.getChildAt(index);
    }

    @Override
    public void removeViewAt(NativeBannerAdView parent, int index) {
        parent.removeViewAt(index);
    }
}