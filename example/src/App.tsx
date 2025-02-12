import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { Digio, Environment } from '@digiotech/react-native';
import type { GatewayEvent } from '@digiotech/react-native';
import { ServiceMode } from '../../src/types/enums/service_mode';

export default function App() {
  React.useEffect(() => {
    const digio = new Digio({ environment: Environment.PRODUCTION, serviceMode: ServiceMode.FP });

    const digioGatewayEventSubscription = digio.addGatewayEventListener(
      (event: GatewayEvent) => {
        console.log('Digio_event ' + event.event);
      }
    );
    digio
      .start(
        'KID250131161454694U3T5A8I8V1IA15',
        'akash.kumar@digio.in',
        'GWT250131161454897Y2PRYLWJCW7VQS'
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
