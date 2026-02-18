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

const config: DigioConfig = { environment: Environment.PRODUCTION, serviceMode: ServiceMode.OTP, isGlobal: false  };
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
    const config: DigioConfig = { environment: Environment.PRODUCTION, serviceMode: ServiceMode.OTP, isGlobal: false };
    const digio = new Digio(config);
    const documentId = "<document_id>";
    const identifier = "<email_or_phone>";
    const tokenId = "<gateway_token_id";
    const digioResponse: DigioResponse = await digio.start(documentId, identifier, tokenId);

  }
}
```

## Android
- Digio SDK supports android version 8 (SDK level 26) and above
- Add below in your project under build.gradle (module:app)file inside dependencies

```tsx
// Required for esign/mandate sign
implementation 'com.github.digio-tech:protean-esign:v3.12'
// under android {} of build.gradle(module:app)
 buildFeatures {
    viewBinding true
    dataBinding true
  }

// Make sure your project using
    compileSdkVersion = 35
    targetSdkVersion = 35
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

## You get a build error due to a theme issue.
- Please add tools:replace="android:theme" under your android manifest.
```
 <application
      ....
      tools:replace="android:theme"
      ...
      >
      .....
    </application>

```
## You get a build error due to a duplicate class android.support.v4
- Check your gradle.properties below should be added.
```
android.enableJetifier=true
reactNativeArchitectures=armeabi-v7a,arm64-v8a,x86,x86_64

```

### IOS Permission
- Digio SDK supports iOS 15.6 and above
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

### Add LSApplicationQueriesSchemes in your iOS app's Info.plist file
```
<key>LSApplicationQueriesSchemes</key>
    <array>
        <string>phonepe</string>
        <string>gpay</string>
        <string>paytmmp</string>
        <string>bhim</string>
        <string>upi</string>
        <string>ppe</string>
    </array>
```

### Proguard:
It is required to test the release build for possible proguard exceptions before prod releases.

```
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface
-keepattributes *Annotation*
-keepattributes Signature
-optimizations !method/inlining/*
-keeppackagenames
-keepnames class androidx.navigation.fragment.NavHostFragment
-keep class * extends androidx.fragment.app.Fragment{}
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-dontwarn androidx.databinding.**
-keep class androidx.databinding.** { *; }
-keepclassmembers class * extends androidx.databinding.** { *; }
-dontwarn org.json.**
-keep class org.json** { *; }
-keep public class org.simpleframework.**{ *; }
-keep class org.simpleframework.xml.**{ *; }
-keep class org.simpleframework.xml.core.**{ *; }
-keep class org.simpleframework.xml.util.**{ *; }
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *; }
-keep class com.google.android.material.** { *; }
-dontwarn org.simpleframework.**
-keepattributes ElementList, Root
-keepclassmembers class * {
    @org.simpleframework.xml.* *;
}
-keep class org.spongycastle.** { *; }
-keep class com.ecs.rdlibrary.request.** { *; }
-keep class com.ecs.rdlibrary.response.** { *; }
-keep class com.ecs.rdlibrary.utils.** { *; }
-keep class com.ecs.rdlibrary.ECSBioCaptureActivity { *; }
-keep class org.simpleframework.xml.** { *; }
-keepattributes Exceptions, InnerClasses
-keep class com.google.android.gms.location.LocationSettingsRequest$Builder { *; }
-keepnames class ** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-keep class * extends androidx.databinding.DataBinderMapper
-dontwarn kotlin.jvm.internal.SourceDebugExtension

-dontwarn org.xmlpull.v1.**
-keep class org.xmlpull.v1.** { *; }
-keep class in.digio.sdk.gateway.interfaces.FeatureRegistrationInterface$DefaultImpls { *; }

-keep class javax.xml.bind.annotation.** { *; }
-dontwarn javax.xml.bind.annotation.**
-keep class com.ecs.cdslxsds.ESignProcessorResponse { *; }

-dontwarn org.xmlpull.v1.XmlPullParser
-dontwarn android.content.res.XmlResourceParser

-keep class in.digio.sdk.gateway.interfaces.FeatureRegistrationInterface$DefaultImpls { *; }
-dontwarn java.lang.invoke.StringConcatFactory
-keep public class com.digio.two_way_sdk.** {
    public *;
}
-keep class com.digio.two_way_sdk.** { *; }
-keep class androidx.databinding.** { *; }
-keep class androidx.viewbinding.** { *; }
-keep public class * extends androidx.**
-keep public class com.google.android.material.** { *; }
-keep public class androidx.** { public *; }
```

