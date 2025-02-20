import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { Digio, Environment, ServiceMode } from '@digiotech/react-native';
import type { GatewayEvent } from '@digiotech/react-native';
// import { ServiceMode } from '../../src/types/enums/service_mode';

export default function App() {
  React.useEffect(() => {
    const digio = new Digio({ environment: Environment.PRODUCTION, serviceMode: ServiceMode.FACE });

    const digioGatewayEventSubscription = digio.addGatewayEventListener(
      (event: GatewayEvent) => {
        console.log('Digio_event ' + event.event);
      }
    );
    digio
      .start(
        'KID250220161217116NNRDJUGPZIPC5U',
        'akash.kumar@digio.in',
        'GWT250220161217149C9DGW6U3F9RXWS'
      )
      .then((res) => {
        console.log(res);
      })
      .catch((err) => console.error(err));

    return () => {
      digioGatewayEventSubscription.remove();
    };
  }, []);

  return (
    <View style={styles.container}>
      <Text>Digio Starting</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
