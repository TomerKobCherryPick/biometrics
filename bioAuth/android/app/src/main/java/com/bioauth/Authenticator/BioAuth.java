package com.bioauth.Authenticator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.*;

import android.content.pm.PackageManager;
import android.app.KeyguardManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;

import com.bioauth.R;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;

import java.util.concurrent.Executor;

import static android.content.Context.KEYGUARD_SERVICE;

public class BioAuth extends ReactContextBaseJavaModule implements DialogInterface.OnClickListener {
    private static ReactApplicationContext reactContext;
    private BiometricPrompt.Builder bioPromptBuilder;
    private BiometricPrompt bioPrompt;
    private CancellationSignal cancelSignal;
    private Executor executor;
    private BiometricPrompt.AuthenticationCallback callback;
    private DialogInterface.OnClickListener onClickListener;

    BioAuth(ReactApplicationContext context) {
        super(context);
        reactContext = context;
        setBioPrompt(context);
    }


    private void setBioPrompt(ReactApplicationContext context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            executor = new Executor() {
                @Override
                public void execute(Runnable command) {
                    command.run();
                }
            };

            bioPromptBuilder = new BiometricPrompt.Builder(context);
            bioPromptBuilder.setTitle("tomer's bio Experiment");
            bioPromptBuilder.setNegativeButton("Cancel", executor, this);
            bioPrompt = bioPromptBuilder.build();
            cancelSignal = new CancellationSignal();

            callback = new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    if ((errorCode == BiometricPrompt.BIOMETRIC_ERROR_NO_BIOMETRICS) ||
                            (errorCode == BiometricPrompt.BIOMETRIC_ERROR_HW_NOT_PRESENT) ||
                            (errorCode == BiometricPrompt.BIOMETRIC_ERROR_HW_UNAVAILABLE) ||
                        (errorCode == BiometricPrompt.BIOMETRIC_ERROR_NO_BIOMETRICS)) {
                        _onNoBiometrics();
                    } else {
                        System.out.println("errorCode: " + errorCode + ", errorString: " + errString);
                    }

                }

                private void _onNoBiometrics() {
                    KeyguardManager km = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
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
                                        // passcode did not succeed
                                    }
                                    context.removeActivityEventListener(this);
                                }
                            }

                        };
                        context.addActivityEventListener(authEventListener);

                        boolean didSucceed = context.startActivityForResult(authIntent, AUTH_REQUEST_CODE, new android.os.Bundle());
                        System.out.println(didSucceed);
                    } else {
                        System.out.println("no authentication on this device");
                        // no authentication on this device
                    }

                }


                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    System.out.println("authentication failed");
                }
                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                    System.out.println("authentication Help : helpCode: " + helpCode + " helpString: " + helpString);
                }
                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    System.out.println("onAuthenticationSucceeded : result: " +  result);
                }


            };
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        // on pressing cancel
        System.out.println("dialog");
    }

    @Override
    public String getName() {
        return "BioAuth";
    }

    @TargetApi(28)
    @ReactMethod
    public void Authenticate() {
        if ((bioPrompt != null) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
            bioPrompt.authenticate(cancelSignal, executor, callback);
        }
    }
}
