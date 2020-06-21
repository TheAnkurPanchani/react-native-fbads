/**
 * NativeAdChoicesView.java
 * com.reactnativefbads
 */
package com.reactnativefbads;

import com.facebook.ads.NativeAd;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.NativeAdBase;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.view.ReactViewGroup;

public class NativeAdChoicesView extends ReactViewGroup {

  private ReactContext mContext;
   public NativeAdChoicesView(ThemedReactContext context) {
    super(context);
    mContext = context;
  }

  public void setNativeAd(NativeAdBase nativeAd) {
    AdChoicesView adChoicesView = new AdChoicesView(mContext, nativeAd, true);
    adChoicesView.measure(adChoicesView.getMeasuredWidth(), adChoicesView.getMeasuredHeight());
    adChoicesView.layout(0, 0, adChoicesView.getMeasuredWidth(), adChoicesView.getMeasuredHeight());
    adChoicesView.bringToFront();
    addView(adChoicesView);
  }
}
