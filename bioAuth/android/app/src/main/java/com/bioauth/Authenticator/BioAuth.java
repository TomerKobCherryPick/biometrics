package com.bioauth.Authenticator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.*;

import android.content.pm.PackageManager;
import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

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

    private FingerprintManager fingerprintManager;
    private AlertDialog fingerPrintDialog;
    private AlertDialog.Builder fingerPrintDialogBuilder;
    private Activity fingerprintActivity;
    BioAuth(ReactApplicationContext context) {
        super(context);
        reactContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setBioPrompt(context);
        } else {
            setFingerprintManager(context);
        }

    }

    @TargetApi(21)
    private void _onNoBiometrics() {
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

    /***
     * api 28 implementation of Biometric authentication
     * @param context - the ReactApplicationContext
     */
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

    @TargetApi(23)
    private void setFingerprintManager(ReactApplicationContext context) {
        fingerprintManager = (FingerprintManager) context.getSystemService(context.FINGERPRINT_SERVICE);
        setFingerPrintAlertDialog(context);
    }
    @TargetApi(23)
    private void setFingerPrintAlertDialog(ReactApplicationContext context) {
        fingerPrintDialogBuilder = new AlertDialog.Builder(context);
        fingerPrintDialogBuilder.setTitle("Fingerprint ID for Tomer's bio experiment");
        fingerPrintDialogBuilder.setCancelable(false);
        fingerPrintDialogBuilder.setPositiveButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

    }

    public void onClick(DialogInterface dialog, int which) {
        // on pressing cancel
        System.out.println("dialog");
    }

    @Override
    public String getName() {
        return "BioAuth";
    }


    @ReactMethod
    public void Authenticate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            authenticateAndroidP();
        } else {
            authenticateBeforeAndroidP();
        }
    }

    private void _showFingerPrintDialog() {
        LayoutInflater inflater = (LayoutInflater) reactContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customLayout = inflater.inflate(R.layout.fingerprint_dialog, null );
        fingerPrintDialogBuilder.setView(customLayout);
        fingerPrintDialog = fingerPrintDialogBuilder.create();
        fingerPrintDialog.getWindow().setType(WindowManager.LayoutParams.
                TYPE_TOAST);
        fingerPrintDialog.show();
    }

    @TargetApi(23)
    private void authenticateBeforeAndroidP() {
        if (fingerprintManager != null) {
            if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
                _showFingerPrintDialog();
                cancelSignal = new CancellationSignal();
                fingerprintManager.authenticate(null, cancelSignal, 0, new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        super.onAuthenticationHelp(helpCode, helpString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }

                }, null);
            } else {
                _onNoBiometrics();
            }
        }
    }

    @TargetApi(28)
    private void authenticateAndroidP() {
        if (bioPrompt != null) {
            bioPrompt.authenticate(cancelSignal, executor, callback);
        }
    }


}
