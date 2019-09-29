package com.bioauth.Authenticator;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.hardware.biometrics.*;
import android.os.Build;
import android.os.CancellationSignal;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;

import java.util.concurrent.Executor;

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
       // bioPrompt.
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

           // bioPromptBuilder.setNegativeButton("Tomer's cancel", executor)
            bioPrompt = bioPromptBuilder.build();

            cancelSignal = new CancellationSignal();
            callback = new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    System.out.println("errorCode: " + errorCode + ", errorString: " + errString);
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
    public void authenticate() {
        if ((bioPrompt != null) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
            bioPrompt.authenticate(cancelSignal, executor, callback);
        }
    }
}
