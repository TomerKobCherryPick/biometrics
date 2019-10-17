package com.bioauth.Authenticator;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.bioauth.R;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;

public class FingerprintAuthenticator {
    private static ReactApplicationContext reactContext;
    private FingerprintManager fingerprintManager;
    private AlertDialog fingerPrintDialog;
    private AlertDialog.Builder fingerPrintDialogBuilder;
    private CancellationSignal cancelSignal;
    private BioAuth.errorTypes error;

    FingerprintAuthenticator(ReactApplicationContext context) {
        reactContext = context;
        setFingerprintManager(context);
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
        fingerPrintDialogBuilder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fingerPrintDialog.hide();
                        cancelSignal.cancel();
                        if (error == BioAuth.errorTypes.Failure) {
                            BioAuth.runOnFailure(error);
                            error = null;
                        }
                    }
                });

            }
        });


    }

    @TargetApi(23)
    protected void authenticate() {
        if (fingerprintManager != null) {
            if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
                this.showFingerPrintDialog();
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
                        reactContext.getCurrentActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fingerPrintDialog.hide();
                                BioAuth.runOnSuccess();
                            }
                        });
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        error = BioAuth.errorTypes.Failure;
                        reactContext.getCurrentActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Window fingerprintDialogWindow = fingerPrintDialog.getWindow();
                                ImageView fingerprintImage = fingerprintDialogWindow.findViewById(R.id.fingerprint_image);
                                TextView fingerprintCta = fingerprintDialogWindow.findViewById(R.id.fingerprint_cta);
                                fingerprintCta.setText("Try Again.");
                                fingerprintImage.setImageResource(R.drawable.fingerprint_fail);
                            }
                        });

                    }

                }, null);
            } else {
              BioAuth.onNoBiometrics();
            }
        }
    }

    private void showFingerPrintDialog() {
        reactContext.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fingerPrintDialogBuilder.setNegativeButton("USE PASSWORD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reactContext.getCurrentActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fingerPrintDialog.hide();
                                cancelSignal.cancel();
                                BioAuth.onNoBiometrics();
                            }
                        });

                    }
                });
                LayoutInflater inflater = (LayoutInflater) reactContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customLayout = inflater.inflate(R.layout.fingerprint_dialog, null);
                fingerPrintDialogBuilder.setView(customLayout);
                fingerPrintDialog = fingerPrintDialogBuilder.create();
                fingerPrintDialog.setOwnerActivity(reactContext.getCurrentActivity());
                fingerPrintDialog.getWindow().setType(WindowManager.LayoutParams.
                        TYPE_TOAST);
                fingerPrintDialog.show();
            }
        });
    }
}
