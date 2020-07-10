package com.reactnativefbads;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class RewardedVideoAdManager extends ReactContextBaseJavaModule implements RewardedVideoAdListener, LifecycleEventListener {

    private Promise mPromise;
    private boolean mLoaded = false;
    private boolean mShowCalled = false;
    private RewardedVideoAd mRewardedAd;

    public RewardedVideoAdManager(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
    }

    @ReactMethod
    public void loadAd(String placementId) {
        if(mRewardedAd != null){
            return;
        }
        ReactApplicationContext reactContext = this.getReactApplicationContext();
        mRewardedAd = new RewardedVideoAd(reactContext, placementId);
        mRewardedAd.setAdListener(this);
        mRewardedAd.loadAd();
    }

    @ReactMethod
    public void showAd(Promise p) {
        if (mPromise != null) {
            p.reject("E_FAILED_TO_SHOW", "Only one `showAd` can be called at once");
            return;
        }
        mPromise = p;
        mShowCalled = true;
        if(mLoaded) {
            if(mRewardedAd.isAdInvalidated()){
                mPromise.reject("E_INVALIDATED","Ad Invalidated");
            } else {
                mRewardedAd.show();
            }
        }
    }

    @Override
    public String getName() {
        return "RnRewardedAdManager";
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        if(mPromise != null) {
            mPromise.reject("E_FAILED_TO_LOAD", adError.getErrorMessage());
        }
        cleanUp();
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (ad == mRewardedAd) {
            mLoaded = true;
            if(mShowCalled) {
                if(mRewardedAd.isAdInvalidated()){
                    mPromise.reject("E_INVALIDATED","Ad Invalidated");
                    cleanUp();
                } else {
                    mRewardedAd.show();
                }
            }
        }
    }

    @Override
    public void onAdClicked(Ad ad) {}

    @Override
    public void onRewardedVideoCompleted() {
        mPromise.resolve(true);
        cleanUp();
    }

    @Override
    public void onRewardedVideoClosed() {
        mPromise.resolve(false);
        cleanUp();
    }

    @Override
    public void onLoggingImpression(Ad ad) {
    }

    private void cleanUp() {
        mPromise = null;
        mLoaded = false;
        mShowCalled = false;

        if (mRewardedAd != null) {
            mRewardedAd.destroy();
            mRewardedAd = null;
        }
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        cleanUp();
    }
}
