package com.bioauth.Authenticator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.app.KeyguardManager;
import android.os.Build;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * This class represents biometric authentication.
 * there are 2 implementations here, one for 23 <= api < 28 , using Fingerprint manager
 * and one for 28 <= api, using Biometric prompt. this is needed since, Fingerprint manager was deprecated
 * in api 28, and Biometric Prompt was added instead.
 * in order for this module to work you need to add the following line to your AndroidManifest.xml:
 *  <uses-permission android:name="android.permission.USE_BIOMETRIC" />
 *  <uses-permission android:name="android.permission.USE_FINGERPRINT" />
 */
public class BioAuth extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private FingerprintAuthenticator fingerprintAuthenticator;
    private BiometricPromptAuthenticator biometricPromptAuthenticator;

    BioAuth(ReactApplicationContext context) {
        super(context);
        reactContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            biometricPromptAuthenticator = new BiometricPromptAuthenticator(context);
        } else {
            fingerprintAuthenticator = new FingerprintAuthenticator(context);
        }
    }

    @TargetApi(21)
    /**
     * this method responsible when there is no biometrics available on the device,
     * it tries to ask for a passcode instead
     */
    protected static void onNoBiometrics() {
        KeyguardManager km = (KeyguardManager) reactContext.getSystemService(KEYGUARD_SERVICE);
        if (km.isKeyguardSecure()) {
            Intent authIntent = km.createConfirmDeviceCredentialIntent("tomer's bio Experiment", null);
            int AUTH_REQUEST_CODE = 1;
            ActivityEventListener authEventListener = new BaseActivityEventListener(){
                @Override
                public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
                    super.onActivityResult(activity, requestCode, resultCode, intent);
                    if (requestCode == AUTH_REQUEST_CODE) {
                        if (resultCode == Activity.RESULT_OK) {
                            // passcode succeeded
                        } else {
                            System.out.println("s");
                            // passcode did not succeed
                        }
                        reactContext.removeActivityEventListener(this);
                    }
                }
            };
            reactContext.addActivityEventListener(authEventListener);
            boolean didSucceed = reactContext.startActivityForResult(authIntent, AUTH_REQUEST_CODE, new android.os.Bundle());
            System.out.println(didSucceed);
        } else {
            System.out.println("no authentication on this device");
            // no authentication on this device
        }
    }

    @Override
    public String getName() {
        return "BioAuth";
    }

    @ReactMethod
    public void Authenticate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            biometricPromptAuthenticator.authenticate();
        } else {
            fingerprintAuthenticator.authenticate();
        }
    }
}
