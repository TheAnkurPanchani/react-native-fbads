export interface NativeBannerAd {
  sponsoredTranslation: string;
  advertiserName: string;
  socialContext: string;
  callToActionText: string;
}

export interface HasNativeBannerAd {
  nativeBannerAd: NativeBannerAd;
}
