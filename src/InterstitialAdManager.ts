import { NativeModules } from 'react-native';

const { CTKInterstitialAdManager } = NativeModules;

export default {
  /**
   * Loads interstitial ad for a given placementId
   */
  loadAd(placementId: string): void {
    CTKInterstitialAdManager.loadAd(placementId);
  },

  /**
   * Shows interstitial ad for a given placementId
   */
  showAd(): Promise<boolean> {
    return CTKInterstitialAdManager.showAd();
  },
};
