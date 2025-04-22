import React, { useState, useEffect } from 'react';
import { StyleSheet, View, Text } from 'react-native';
import { Digio, Environment, ServiceMode } from '@digiotech/react-native';
import type { GatewayEvent } from '@digiotech/react-native';

export default function App() {
  const [digioResult, setDigioResult] = useState<any | null>(null);
  const [digioEvent, setDigioEvent] = useState<string | null>(null);

  useEffect(() => {
    const digio = new Digio({ environment: Environment.PRODUCTION, serviceMode: ServiceMode.OTP });

    const digioGatewayEventSubscription = digio.addGatewayEventListener(
      (event: GatewayEvent) => {
        console.log('Digio_event ' + event.event);
        if (event.event !== undefined) {
          setDigioEvent(event.event);
        }
      }
    );

    digio
      .start(
        'DID25042217573519794QGI9P27RLKLF',
        'akash.kumar@digio.in',
        'GWT250422175735425I7RZLI1K4OTV9S'
      )
      .then((res) => {
        console.log(res);
        if (res !== undefined) {
          setDigioResult(res);
        }
      })
      .catch((err) => console.error(err));

    return () => {
      digioGatewayEventSubscription.remove();
    };
  }, []);

  return (
    <View style={styles.container}>
      <Text>Digio Starting</Text>
      <View style={styles.resultContainer}>
        <Text>Result:</Text>
        <Text>{digioResult ? JSON.stringify(digioResult) : 'Waiting...'}</Text>
      </View>
      <View style={styles.eventContainer}>
        <Text>Event:</Text>
        <Text>{digioEvent ? digioEvent : 'Waiting...'}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  resultContainer: {
    marginTop: 20,
    padding: 10,
    backgroundColor: '#f0f0f0',
    borderRadius: 5,
  },
  eventContainer: {
    marginTop: 20,
    padding: 10,
    backgroundColor: '#e0e0e0',
    borderRadius: 5,
  },
});
