package com.bioauth.Authenticator;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;

import java.util.concurrent.Executor;

public class BiometricPromptAuthenticator {
    private static ReactApplicationContext reactContext;
    private BiometricPrompt.Builder bioPromptBuilder;
    private BiometricPrompt bioPrompt;
    private CancellationSignal cancelSignal;
    private Executor executor;
    private BiometricPrompt.AuthenticationCallback callback;
    private DialogInterface.OnClickListener onClickListener;
    private BioAuth.errorTypes error;

    BiometricPromptAuthenticator(ReactApplicationContext context) {
        setBioPrompt(context);
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
            bioPromptBuilder.setNegativeButton("Cancel", executor, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // on pressing cancel
                    System.out.println("dialog");
                    if (error == BioAuth.errorTypes.Failure) {
                        BioAuth.onFailure.invoke(BioAuth.errorTypes.Failure.toString());
                        error = null;

                    }

                }
            });



        }
    }

    @TargetApi(28)
    protected void authenticate(Callback onSuccess, Callback onFailure) {
        bioPrompt = bioPromptBuilder.build();
        cancelSignal = new CancellationSignal();
        if (bioPrompt != null) {
            callback = new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    if ((errorCode == BiometricPrompt.BIOMETRIC_ERROR_NO_BIOMETRICS) ||
                            (errorCode == BiometricPrompt.BIOMETRIC_ERROR_HW_NOT_PRESENT) ||
                            (errorCode == BiometricPrompt.BIOMETRIC_ERROR_HW_UNAVAILABLE) ||
                            (errorCode == BiometricPrompt.BIOMETRIC_ERROR_NO_BIOMETRICS)) {
                        BioAuth.onNoBiometrics(onSuccess, onFailure);
                    } else {
                        System.out.println("errorCode: " + errorCode + ", errorString: " + errString);
                        onFailure.invoke(BioAuth.errorTypes.Failure.toString());
                    }
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    error = BioAuth.errorTypes.Failure;
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
                    onSuccess.invoke();
                }
            };
            bioPrompt.authenticate(cancelSignal, executor, callback);
        }
    }

}
