package com.reactnativefbads;

import android.util.Log;
import android.view.View;

import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdsManager;
import com.facebook.ads.NativeBannerAd;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIManagerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ReactModule(name="NativeBannerAdManager")
public class NativeBannerAdManager extends ReactContextBaseJavaModule {
  private Map<String, NativeBannerAd> mNativeBannerAds = new HashMap<>();

  public NativeBannerAdManager(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "NativeBannerAdManager";
  }

  /**
   * Helper for sending events back to Javascript.
   *
   * @param eventName
   * @param params
   */
  private void sendAppEvent(String eventName, Object params) {
    ReactApplicationContext context = this.getReactApplicationContext();

    if (context == null || !context.hasActiveCatalystInstance()) {
      return;
    }

    context
        .getJSModule(RCTNativeAppEventEmitter.class)
        .emit(eventName, params);
  }

  public void setNativeBannerAd(final String placementId, final NativeBannerAd mNativeBannerAd) {
    mNativeBannerAds.put(placementId, mNativeBannerAd);
  }

  public NativeBannerAd getNativeBannerAd(final String placementId) {
    return mNativeBannerAds.get(placementId);
  }

  @ReactMethod
  public void registerViewsForInteraction(final int adTag,
                                          final int mediaViewTag,
                                          final ReadableArray clickableViewsTags,
                                          final Promise promise) {
    getReactApplicationContext().getNativeModule(UIManagerModule.class).addUIBlock(new UIBlock() {
      @Override
      public void execute(NativeViewHierarchyManager nativeViewHierarchyManager) {
        try {
          NativeBannerAdView nativeAdView = null;
          MediaView mediaView = null;

          if (adTag != -1) {
            nativeAdView = (NativeBannerAdView) nativeViewHierarchyManager.resolveView(adTag);
          }

          if (mediaViewTag != -1) {
            mediaView = (MediaView) nativeViewHierarchyManager.resolveView(mediaViewTag);
          }

          List<View> clickableViews = new ArrayList<>();

          for (int i = 0; i < clickableViewsTags.size(); ++i) {
            View view = nativeViewHierarchyManager.resolveView(clickableViewsTags.getInt(i));
            clickableViews.add(view);
          }

          nativeAdView.registerViewsForInteraction(mediaView, clickableViews);
          promise.resolve(null);

        } catch (ClassCastException e) {
          promise.reject("E_CANNOT_CAST", e);
        } catch (IllegalViewOperationException e) {
          promise.reject("E_INVALID_TAG_ERROR", e);
        } catch (NullPointerException e) {
          promise.reject("E_NO_NATIVE_AD_VIEW", e);
        } catch (Exception e) {
          promise.reject("E_AD_REGISTER_ERROR", e);
        }
      }
    });
  }
}
