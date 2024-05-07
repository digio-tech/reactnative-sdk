import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { Digio, Environment } from '@digiotech/react-native';
import type { GatewayEvent } from '@digiotech/react-native';

export default function App() {
  React.useEffect(() => {
    const digio = new Digio({ environment: Environment.SANDBOX });

    const digioGatewayEventSubscription = digio.addGatewayEventListener(
      (event: GatewayEvent) => {
        console.log('Digio_event ' + event.event);
      }
    );
    digio
      .start(
        'DID240507182157944L5FEM5BAFL6SCU',
        'akash.kumar@digio.in',
        'GWT240507182158398221W78VJFJNL1U'
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
