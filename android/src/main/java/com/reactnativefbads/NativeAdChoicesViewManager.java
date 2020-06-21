/**
 * NativeAdChoiceViewManager.java
 * com.reactnativefbads
 */

package com.reactnativefbads;

import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdsManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

public class NativeAdChoicesViewManager extends SimpleViewManager<NativeAdChoicesView> {
  ReactApplicationContext mReactContext;

  private static final String REACT_CLASS = "AdChoicesView";

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  public NativeAdChoicesViewManager(ReactApplicationContext context) {
    super();
    mReactContext = context;
  }

  @ReactProp(name = "bannerPlacementId")
  public void setNativeBannerAd(NativeAdChoicesView view, String bannerPlacementId) {
    if(bannerPlacementId != null) {
      final NativeBannerAdManager adManager = mReactContext.getNativeModule(NativeBannerAdManager.class);
      view.setNativeAd(adManager.getNativeBannerAd(bannerPlacementId));
    }
  }

  @ReactProp(name = "placementId")
  public void adsManager(NativeAdChoicesView view, String adsManagerId) {
    if(adsManagerId != null) {
      NativeAdManager adManager = mReactContext.getNativeModule(NativeAdManager.class);
      NativeAdsManager adsManager = adManager.getFBAdsManager(adsManagerId);

      view.setNativeAd(adsManager.nextNativeAd());
    }
  }

  @Override
  protected NativeAdChoicesView createViewInstance(ThemedReactContext reactContext) {
    return new NativeAdChoicesView(reactContext);
  }
}