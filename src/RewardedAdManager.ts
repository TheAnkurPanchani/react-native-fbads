import { NativeModules } from 'react-native';

const { RnRewardedAdManager } = NativeModules;

export default {
  /**
   * Loads rewarded ad for a given placementId
   */
  loadAd(placementId: string): void {
    RnRewardedAdManager.loadAd(placementId);
  },

  /**
   * Shows rewarded ad for a given placementId
   */
  showAd(): Promise<boolean> {
    return RnRewardedAdManager.showAd();
  },
};
