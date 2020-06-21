import React from 'react';
import { StyleProp, ViewStyle, requireNativeComponent, StyleSheet, Platform } from 'react-native';

import { AdChoicesViewContext, AdChoicesViewContextValueType } from './contexts';

interface AdChoicesProps {
  location?: AdChoiceLocation;
  expandable?: boolean;
  style?: StyleProp<ViewStyle>;
  isNativeBanner?: boolean;
}

// tslint:disable-next-line:variable-name
const NativeAdChoicesView = requireNativeComponent('AdChoicesView');

type AdChoiceLocation = 'topLeft' | 'topRight' | 'bottomLeft' | 'bottomRight';

export default class AdChoicesViewManager extends React.Component<AdChoicesProps> {
  static defaultProps: AdChoicesProps = {
    location: 'topLeft',
    expandable: false,
  };

  render() {
    const { isNativeBanner } = this.props;
    return (
      <AdChoicesViewContext.Consumer>
        {(placementId: AdChoicesViewContextValueType) => (
          <NativeAdChoicesView
            style={[styles.adChoice, this.props.style]}
            placementId={!!isNativeBanner ? undefined : placementId}
            bannerPlacementId={!!isNativeBanner ? placementId : undefined}
            location={this.props.location}
            expandable={this.props.expandable}
          />
        )}
      </AdChoicesViewContext.Consumer>
    );
  }
}

const styles = StyleSheet.create({
  adChoice: {
    backgroundColor: 'transparent',
    ...Platform.select({
      ios: {
        width: 0,
        height: 0,
      },
      android: {
        width: 22,
        height: 22,
      },
    }),
  },
});
