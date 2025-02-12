package com.digioreactnative;

import android.util.Log;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import in.digio.sdk.gateway.DigioActivity;
import in.digio.sdk.gateway.DigioConstants;
import in.digio.sdk.gateway.enums.DigioEnvironment;
import in.digio.sdk.gateway.enums.DigioServiceMode;
import in.digio.sdk.gateway.enums.DigioErrorCode;
import in.digio.sdk.gateway.model.DigioConfig;
import in.digio.sdk.gateway.model.DigioTheme;

@ReactModule(name = DigioReactNativeModule.NAME)
public class DigioReactNativeModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
  public static final String NAME = "DigioReactNative";

  public static final String AAR_VERSION = "4.0.9";
  public static final int DIGIO_ACTIVITY = 73457843;
  private Promise resultPromise;
  private boolean isReceiverRegistered = false;


  private BroadcastReceiver eventBroadcastReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getStringExtra("data") != null) {
        JSONObject jsonObject = null;
        try {
          jsonObject = new JSONObject(
            intent.getStringExtra(
              "data"
            )
          );
        } catch (JSONException e) {
          e.printStackTrace();
        }
        WritableMap resultMap = MapUtil.jsonToWritableMap(jsonObject);
        getReactApplicationContext()
          .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
          .emit("gatewayEvent", resultMap);
      }
    }
  };

  ActivityEventListener activityEventListener = new ActivityEventListener() {
    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent intent) {
      if (requestCode == DIGIO_ACTIVITY) {
        onNativeActivityResult(resultCode, intent);
      }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }
  };

  private void onNativeActivityResult(int resultCode, Intent data) {
    WritableMap resultMap = Arguments.createMap();
    resultMap.putInt("code", resultCode);
    if (data != null) {
      resultMap.putString("message", data.getStringExtra("message"));
      String screenName = data.getStringExtra("screen_name");
      if (TextUtils.isEmpty(screenName)) {
        screenName = "starting_digio";
      }
      resultMap.putString("screen", screenName);
      resultMap.putString("step", data.getStringExtra("step"));
      resultMap.putString("documentId", data.getStringExtra("document_id"));
      resultMap.putString("failingUrl", data.getStringExtra("failing_url"));
      resultMap.putInt("errorCode", data.getIntExtra("error_code", resultCode));
      String[] stringArrayExtra = data.getStringArrayExtra("permissions");
      WritableArray permissionArray = Arguments.createArray();
      if (stringArrayExtra != null && stringArrayExtra.length > 0) {
        for (String permission : stringArrayExtra) {
          permissionArray.pushString(permission);
        }
      }
      resultMap.putArray(
        "permissions", permissionArray);
    }
    if (resultCode == DigioConstants.RESPONSE_CODE_SUCCESS) {
      if (resultMap.getString("message") == null) {
        resultMap.putString("message", "KYC Success");
      }
    } else if (resultCode == DigioErrorCode.DIGIO_PERMISSIONS_REQUIRED.getErrorCode()) {
      resultMap.putInt("errorCode", resultCode);
    } else {
      if (resultMap.getString("message") == null) {
        resultMap.putString("message", "KYC Failure");
      }
      resultMap.putInt("errorCode", resultCode);
    }
    resultPromise.resolve(resultMap);
  }

  @SuppressLint("WrongConstant")
  public DigioReactNativeModule(ReactApplicationContext reactContext) {
    super(reactContext);
    reactContext.addActivityEventListener(activityEventListener);
    this.getReactApplicationContext().addLifecycleEventListener(this);
  }

  @Override
  public void onHostResume() {
    if (!isReceiverRegistered) {
      IntentFilter filter = new IntentFilter(DigioConstants.GATEWAY_EVENT);
      // this.getReactApplicationContext().registerReceiver(eventBroadcastReceiver, filter);
      compatRegisterReceiver(this.getReactApplicationContext(), eventBroadcastReceiver, filter, true);
      isReceiverRegistered = true;
    }
    
  }

  @Override
  public void onHostPause() {
    // this.getReactApplicationContext().unregisterReceiver(eventBroadcastReceiver);
  }

  @Override
  public void onHostDestroy() {
   if (isReceiverRegistered) {
        this.getReactApplicationContext().unregisterReceiver(eventBroadcastReceiver);
        isReceiverRegistered = false;
     }
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  private void compatRegisterReceiver(Context context, BroadcastReceiver receiver,
   IntentFilter filter, boolean exported) {
    if (Build.VERSION.SDK_INT >= 34 && context.getApplicationInfo().targetSdkVersion >= 34) {
      context.registerReceiver(
          receiver, filter, exported ? Context.RECEIVER_EXPORTED : Context.RECEIVER_NOT_EXPORTED);
    } else {
      context.registerReceiver(receiver, filter);
    }
  }

  @ReactMethod
  public void start(String documentId, String identifier, String tokenId, ReadableMap additionalData, ReadableMap config, Promise promise) {
    this.resultPromise = promise;
    try {
      Intent intent = new Intent(this.getCurrentActivity(), DigioActivity.class);
      DigioConfig digioConfig = new DigioConfig();
      String environment = config.getString("environment");
      String logo = config.getString("logo");
      String mode = config.getString("mode");
      Log.e("Digio_mode ", ""+mode);
      if (!TextUtils.isEmpty(environment)) {
        try {
          digioConfig.setEnvironment(DigioEnvironment.valueOf(environment.toUpperCase(Locale.ENGLISH)));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (!TextUtils.isEmpty(logo)) {
        digioConfig.setLogo(logo);
      }
      DigioTheme digioTheme = new DigioTheme();

      String primaryColor = config.getString("primaryColor");
      if (!TextUtils.isEmpty(primaryColor)) {
        digioTheme.setPrimaryColorHex(primaryColor);
      }
      String secondaryColor = config.getString("secondaryColor");
      if (!TextUtils.isEmpty(secondaryColor)) {
        digioTheme.setSecondaryColorHex(secondaryColor);
      }
      String fontFormat = config.getString("fontFormat");
      if (!TextUtils.isEmpty(fontFormat)) {
        digioTheme.setFontFormat(fontFormat);
      }
      String fontFamily = config.getString("fontFamily");
      if (!TextUtils.isEmpty(fontFamily)) {
        digioTheme.setFontFamily(fontFamily);
      }
      String fontUrl = config.getString("fontUrl");
      if (!TextUtils.isEmpty(fontUrl)) {
        digioTheme.setFontUrl(fontUrl);
      }

      digioConfig.setTheme(digioTheme);
      
      intent.putExtra("aar_version", AAR_VERSION);
      if (documentId.startsWith("ENA") || documentId.startsWith("DID")) {
        intent.putExtra("navigation_graph", in.digio.sdk.esign.R.navigation.esign);
        if (!TextUtils.isEmpty(mode)) {
        digioConfig.setServiceMode(DigioServiceMode.valueOf(mode.toUpperCase(Locale.ENGLISH)));
      }
      } else {
        intent.putExtra("navigation_graph", in.digio.sdk.kyc.R.navigation.workflow);
      }
      intent.putExtra("config", digioConfig);
      intent.putExtra("document_id", documentId);
      intent.putExtra("customer_identifier", identifier);
      intent.putExtra("token_id", tokenId);
      HashMap additionalDataMap = new HashMap();
      if (additionalData != null) {
        Iterator<Map.Entry<String, Object>> entryIterator = additionalData.getEntryIterator();
        while (entryIterator.hasNext()) {
          Map.Entry<String, Object> objectEntry = entryIterator.next();
          additionalDataMap.put(objectEntry.getKey(), objectEntry.getValue());
        }
      }
      intent.putExtra("additional_data", additionalDataMap);
      this.getCurrentActivity().startActivityForResult(intent, DIGIO_ACTIVITY);
    } catch (Exception e) {
      // Throws DigioException if WorkflowResponseListener is not implemented/passed, or
      // DigioConfig is not valid (check config parameters)
      // It is mandatory to implemented/add WorkflowResponseListener
      e.printStackTrace();
      resultPromise.reject(e);
    }
  }
}
