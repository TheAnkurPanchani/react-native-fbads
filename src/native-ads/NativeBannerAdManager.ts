import { NativeModules } from 'react-native';

const { NativeBannerAdManager } = NativeModules;

export default class NativeBannerAdsManager {
  static async registerViewsForInteractionAsync(
    nativeBannerAdViewTag: number,
    mediaViewTag: number,
    clickable: number[]
  ) {
    if (mediaViewTag > 0) {
      clickable.push(mediaViewTag);
    }
    const result = await NativeBannerAdManager.registerViewsForInteraction(
      nativeBannerAdViewTag,
      mediaViewTag,
      clickable
    );
    return result;
  }
}
