import {
  DeviceEventEmitter,
  NativeEventEmitter,
  NativeModules,
  Platform,
} from 'react-native';
import type { EmitterSubscription } from 'react-native';
import type { DigioConfig } from './types/interfaces/digio_config';
import type { DigioResponse } from './types/interfaces/digio_response';
import type { GatewayEvent } from './types/interfaces/gateway_event';

export { Environment } from './types/enums/environment';
export { ServiceMode } from './types/enums/service_mode';

const LINKING_ERROR =
  `The package 'digio-react-native' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const DigioReactNative = NativeModules.DigioReactNative
  ? NativeModules.DigioReactNative
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

type GatewayEventCallbackFn = (data: GatewayEvent) => void;

export type {
  DigioConfig,
  DigioResponse,
  GatewayEvent,
  GatewayEventCallbackFn,
};

export class Digio {
  private readonly config: DigioConfig;

  constructor(config: DigioConfig) {
    this.config = config;
  }

  addGatewayEventListener(
    callback: GatewayEventCallbackFn
  ): EmitterSubscription {
    if (Platform.OS === 'android') {
      return DeviceEventEmitter.addListener('gatewayEvent', (data) => {
        if (callback) {
          callback(data);
        }
      });
    }
    if (Platform.OS === 'ios') {
      const digioReactNativeEmitter = new NativeEventEmitter(DigioReactNative);
      return digioReactNativeEmitter.addListener('gatewayEvent', (data) => {
        if (callback) {
          callback(data);
        }
      });
    }
    throw Error(`Platform ${Platform.OS} not supported`);
  }

  async start(
    documentId: string,
    identifier: string,
    tokenId?: string,
    additionalData?: { [key: string]: string }
  ): Promise<DigioResponse> {
    return DigioReactNative.start(
      documentId,
      identifier,
      tokenId ?? null,
      additionalData ?? null,
      this.buildConfigParams() ?? null
    );
  }

  private buildConfigParams() {
    if (!this.config) return;
    return {
      environment: this.config.environment,
      logo: this.config.logo,
      mode: this.config.serviceMode,
      ...this.config.theme,
    };
  }
}
