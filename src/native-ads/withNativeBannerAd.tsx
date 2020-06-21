import { EventSubscription } from 'fbemitter';
import React, { ReactNode } from 'react';
import { findNodeHandle, requireNativeComponent } from 'react-native';

import { areSetsEqual } from '../util/areSetsEqual';
import {
  ComponentOrClass,
  MediaViewContext,
  MediaViewContextValueType,
  TriggerableContext,
  TriggerableContextValueType,
  AdChoicesViewContext,
} from './contexts';
import { NativeMediaView as MediaView } from './MediaViewManager';
import { HasNativeBannerAd, NativeBannerAd } from './NativeBannerAd';
import NativeBannerAdsManager from './NativeBannerAdManager';

// tslint:disable-next-line:variable-name
const NativeBannerAdView = requireNativeComponent('NativeBannerAd');

interface AdWrapperState {
  ad?: NativeBannerAd;
  mediaViewNodeHandle: number;
  clickableChildren: Set<number>;
}

interface AdWrapperProps {
  placementId: string;
  onAdLoaded?: (ad: NativeBannerAd) => void;
}

export default <T extends HasNativeBannerAd>(
  // tslint:disable-next-line:variable-name
  Component: React.ComponentType<T>
) =>
  class NativeBannerAdWrapper extends React.Component<AdWrapperProps & T, AdWrapperState> {
    private subscription?: EventSubscription;
    private subscriptionError?: EventSubscription;
    private nativeBannerAdViewRef?: React.Component;
    private registerFunctionsForTriggerables: TriggerableContextValueType;
    private registerFunctionsForMediaView: MediaViewContextValueType;
    private clickableChildrenNodeHandles: Map<ComponentOrClass, number>;

    constructor(props: AdWrapperProps & T) {
      super(props);

      this.registerFunctionsForTriggerables = {
        register: this.registerClickableChild,
        unregister: this.unregisterClickableChild,
      };

      this.registerFunctionsForMediaView = {
        unregister: this.unregisterMediaView,
        register: this.registerMediaView,
      };

      this.clickableChildrenNodeHandles = new Map();

      this.state = {
        // iOS requires a non-null value
        mediaViewNodeHandle: -1,
        clickableChildren: new Set(),
      };
    }

    public componentDidUpdate(_: AdWrapperProps, prevState: AdWrapperState) {
      if (this.state.mediaViewNodeHandle === -1) {
        // Facebook's SDK requires MediaView reference in order to register
        // interactable views. If it is missing, we can't proceed with the registration.
        return;
      }

      const mediaViewNodeHandleChanged =
        this.state.mediaViewNodeHandle !== prevState.mediaViewNodeHandle;
      const clickableChildrenChanged = areSetsEqual(
        prevState.clickableChildren,
        this.state.clickableChildren
      );

      if (mediaViewNodeHandleChanged || clickableChildrenChanged) {
        const viewHandle = findNodeHandle(this.nativeBannerAdViewRef!);
        if (!viewHandle) {
          // Skip registration if the view is no longer valid.
          return;
        }

        NativeBannerAdsManager.registerViewsForInteractionAsync(
          viewHandle,
          this.state.mediaViewNodeHandle,
          [...this.state.clickableChildren]
        );
      }
    }

    /**
     * Clear subscription when component goes off screen
     */
    public componentWillUnmount() {
      if (this.subscription) {
        this.subscription.remove();
      }
      if (this.subscriptionError) {
        this.subscriptionError.remove();
      }
    }

    private registerMediaView = (mediaView: ComponentOrClass) =>
      this.setState({ mediaViewNodeHandle: findNodeHandle(mediaView) || -1 });
    private unregisterMediaView = () => this.setState({ mediaViewNodeHandle: -1 });

    private registerClickableChild = (child: ComponentOrClass) => {
      const handle = findNodeHandle(child);

      if (!handle) {
        return;
      }

      this.clickableChildrenNodeHandles.set(child, handle);

      this.setState({
        clickableChildren: this.state.clickableChildren.add(handle),
      });
    };

    private unregisterClickableChild = (child: ComponentOrClass) => {
      this.setState(({ clickableChildren }) => {
        const newClickableChildren = new Set(clickableChildren);
        newClickableChildren.delete(this.clickableChildrenNodeHandles.get(child)!);
        this.clickableChildrenNodeHandles.delete(child);
        return { clickableChildren: newClickableChildren };
      });
    };

    private handleAdUpdated = () =>
      this.state.ad && this.props.onAdLoaded && this.props.onAdLoaded(this.state.ad);

    private handleAdLoaded = ({ nativeEvent }: { nativeEvent: NativeBannerAd }) => {
      this.setState({ ad: nativeEvent }, this.handleAdUpdated);
    };

    private handleNativeBannerAdViewMount = (ref: React.Component) => {
      this.nativeBannerAdViewRef = ref;
    };

    private renderAdComponent(componentProps: T): ReactNode {
      const { ad } = this.state;
      if (!ad) {
        return null;
      }
      return (
        <MediaViewContext.Provider value={this.registerFunctionsForMediaView}>
          <TriggerableContext.Provider value={this.registerFunctionsForTriggerables}>
            <AdChoicesViewContext.Provider value={this.props.placementId}>
              {/* Facebook's registerViewForInteraction requires MediaView
                  references to be set. We include it as a default */}
              <MediaView style={{ width: 0, height: 0 }} />
              <Component {...componentProps} nativeBannerAd={ad} />
            </AdChoicesViewContext.Provider>
          </TriggerableContext.Provider>
        </MediaViewContext.Provider>
      );
    }

    render() {
      // Cast to any until https://github.com/Microsoft/TypeScript/issues/10727 is resolved
      const { placementId, onAdLoaded, ...rest } = this.props as any;

      return (
        <NativeBannerAdView
          ref={this.handleNativeBannerAdViewMount}
          placementId={placementId}
          onAdLoaded={this.handleAdLoaded}>
          {this.renderAdComponent(rest)}
        </NativeBannerAdView>
      );
    }
  };
