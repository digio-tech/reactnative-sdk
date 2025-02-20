# Digio React Native SDK

[![npm version](https://badge.fury.io/js/@digiotech%2Freact-native.svg)](https://badge.fury.io/js/@digiotech%2Freact-native)

Official React Native SDK for Digio Gateway Integration

## Installation

```sh
yarn install @digiotech/react-native
```

## Documentation

Documentation of Digio Gateway Integration and their usage

### Basic Usage

Instantiate the Digio instance with `environment` & other options

```tsx
import { Digio, DigioConfig, DigioResponse, ServiceMode } from '@digiotech/react-native';

const config: DigioConfig = { environment: Environment.PRODUCTION, serviceMode: ServiceMode.OTP  };
const digio = new Digio(config);
const documentId = "<document_id>";
const identifier = "<email_or_phone>";
const digioResponse: DigioResponse = await digio.start(documentId, identifier);
```

### Consuming gateway events [Optional]

You can consume events and understand the flow or the journey of the user as he is performing it.

For a complete list of events and the payload associated with it, refer [here](https://docs.google.com/document/d/15LHtjGyXd_JNM0de8uH9zB7WllJikRl1d9e4qdy0-C0/edit?usp=sharing)

```tsx
import { useEffect } from 'react';
import { Digio, DigioConfig, GatewayEvent, ServiceMode } from '@digiotech/react-native';

function YourComponent() {
  useEffect(() => {
    const gatewayEventListener = digio.addGatewayEventListener(
      (event: GatewayEvent) => {
        // Do some operation on the received events
      }
    );

    return () => {
      gatewayEventListener.remove();
    }
  }, []);
}
```

### Complete Usage

```tsx
import { useEffect } from 'react';
import { Digio, DigioConfig, GatewayEvent, ServiceMode } from '@digiotech/react-native';

function YourComponent() {
  useEffect(() => {
    const gatewayEventListener = digio.addGatewayEventListener(
      (event: GatewayEvent) => {
        // Do some operation on the received events
      }
    );

    return () => {
      gatewayEventListener.remove();
    }
  }, []);

  const triggerDigioGateway = async () => {
    const config: DigioConfig = { environment: Environment.PRODUCTION, serviceMode: ServiceMode.OTP };
    const digio = new Digio(config);
    const documentId = "<document_id>";
    const identifier = "<email_or_phone>";
    const tokenId = "<gateway_token_id";
    const digioResponse: DigioResponse = await digio.start(documentId, identifier, tokenId);

  }
}
```

## Android
- Add below in your project under build.gradle (module:app)file inside dependencies

```tsx
// Required for esign/mandate sign
implementation 'com.github.digio-tech:protean-esign:v3.2'
// under android {} of build.gradle(module:app)
 buildFeatures {
    viewBinding true
    dataBinding true
  }

```

## SDK Reference

### DigioConfig

**Parameters:**

| Name            | Type    | Description                                                                                 |
|-----------------|---------|---------------------------------------------------------------------------------------------|
| environment*    | string  | Environment for which you want to open gateway. One of `sandbox` or `production`            |
| logo            | string  | Pass an URL of your brand logo. **Note:** Pass an optimised image url for best results      |
| theme           | string  | Options for changing the appearance of the gateway. See below for options under it.         |
| serviceMode*    | string  | ServiceMode can be OTP/FACE/IRIS/FP <Default it's OTP>  you can set based on your requirment|

**Theme:**

| Name           | Type    | Description                                                              |
|----------------|---------|--------------------------------------------------------------------------|
| primaryColor   | string  | Your brand colour's HEX code to alter CTA (call-to-action) button colors |
| secondaryColor | string  | HEX Code to alter text colors                                            |
| fontFamily     | string  | Font Family of your choice. For eg: Crimson Pro                          |
| fontFormat     | string  | Format of the Font Family Provided. For eg: ’woff2’,’ot’,’tt’            |
| fontUrl        | string  | Font Family URL. For eg: '{font_family_url}.woff2'                       |

### DigioResponse [Response received from Gateway]

| Name        | Type          | Description                                                                                                                                                                                                 |
|-------------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| code*       | number        | SUCCESS = 1001 <br /> FAIL = 1002 <br /> CANCEL = -1000 <br /> WEBVIEW_CRASH = 1003 <br /> WEBVIEW_ERROR = 10017 <br /> SDK_CRASH = 1004 <br /> Location/Camera/MicroPhone Permission Denied By User = 1008 |
| documentId  | string        | Document ID Passed from the parent app. For eg: `KID22040413040490937VNTC6LAP8KWD`                                                                                                                          |
| message     | string        | Error message in case of crash or failure                                                                                                                                                                   |
| permissions | Array<string> | List of mandatory permissions which are not given during kyc journey                                                                                                                                        |

### Android Permissions

Add required permissions in the manifest file. Note - This is the common SDK for various KYC flows

```
<!--RECORD_AUDIO and MODIFY_AUDIO_SETTINGS Permission required for Video KYC -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
/** Required for geotagging */
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

/** Required for ID card analysis, selfie and face match**/
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera.autofocus"   android:required="false" />

<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />


```
A fintech Android app can't access the following permission
- Read_external_storage
- Read_media_images
- Read_contacts
- Access_fine_location
- Read_phone_numbers
- Read_media_videos

### IOS Permission

Permissions need to add in your info.plist
```
  /**  Camera permission incase of selfie/video KYC/ capture document  **/
  <key>NSCameraUsageDescription</key>
  <string>$(PRODUCT_NAME) would like to access your camera.</string>
  <key>NSPhotoLibraryUsageDescription</key>
  <string>$(PRODUCT_NAME) would like to access your photo.</string>
  /**  Microphone permission incase of video KYC  **/
  <key>NSMicrophoneUsageDescription</key>
  <string>$(PRODUCT_NAME) would like to access your microphone to capture video.</string>
  /** Location permission for geo tagging for camera/video kyc/ selfie **/
  <key>NSLocationWhenInUseUsageDescription</key>
  <string>$(PRODUCT_NAME) would like to access your location.</string>
  <key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
  <string>$(PRODUCT_NAME) would like to access your location.</string>
```
Note : All permissions should be checked and taken before triggering Digio SDK
