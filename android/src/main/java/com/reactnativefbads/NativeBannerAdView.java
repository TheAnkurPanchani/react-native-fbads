package com.reactnativefbads;

import android.view.View;

import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeBannerAd;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.view.ReactViewGroup;

import java.util.List;

public class NativeBannerAdView extends ReactViewGroup {
  /**
   * @{NativeBannerAd} BannerAd Instance
   **/
  private NativeBannerAd mNativeBannerAd;

  /**
   * @{RCTEventEmitter} instance used for sending events back to JS
   **/
  private RCTEventEmitter mEventEmitter;

  /**
   * Creates new NativeAdView instance and retrieves event emitter
   *
   * @param context
   */
  public NativeBannerAdView(ThemedReactContext context) {
    super(context);

    mEventEmitter = context.getJSModule(RCTEventEmitter.class);
  }

  /**
   * Sends serialised version of a native ad back to Javascript.
   *
   * @param nativeBannerAd
   */
  public void setNativeBannerAd(NativeBannerAd nativeBannerAd) {
    if (mNativeBannerAd != null) {
      mNativeBannerAd.unregisterView();
    }

    mNativeBannerAd = nativeBannerAd;

    if (nativeBannerAd == null) {
      mEventEmitter.receiveEvent(getId(), "onAdLoaded", null);
      return;
    }

    WritableMap event = Arguments.createMap();
    event.putString("advertiserName", nativeBannerAd.getAdvertiserName());
    event.putString("socialContext", nativeBannerAd.getAdSocialContext());
    event.putString("callToActionText", nativeBannerAd.getAdCallToAction());
    event.putString("sponsoredTranslation", nativeBannerAd.getSponsoredTranslation());

    mEventEmitter.receiveEvent(getId(), "onAdLoaded", event);
  }

  public void registerViewsForInteraction(MediaView mediaView, List<View> clickableViews) {
    mNativeBannerAd.registerViewForInteraction(this, mediaView, clickableViews);
  }

}
