package de.jopa.qrcodescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.CodeScanner;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ScanQRCodeActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        if (isCameraPermissionGranted()) {
            CodeScannerView scannerView = findViewById(R.id.scanner_view);
            mCodeScanner = new CodeScanner(this, scannerView);
            mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
                onPause();
                send(result.getText());
            }));
            scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
    private void send(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRCodeActivity.this);
        builder.setTitle(R.string.app_name);
        TextView textView = new TextView(ScanQRCodeActivity.this);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(20);
        textView.setPadding(0, 30, 0, 0);
        textView.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.chooseApp));
            startActivity(shareIntent);
        });
        builder.setView(textView);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.open), (dialog, which) -> {
            try {
                if (!(text.contains("WIFI"))) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_VIEW);

                    String callback = getIntent().getStringExtra("callback");
                    if (callback == null) {
                        sendIntent.setData(Uri.parse(text));
                        startActivity(sendIntent);
                    } else {
                        String encodedText;
                        try {
                            encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
                        } catch (UnsupportedEncodingException e) {
                            encodedText = text;
                        }
                        if ("=".equals(callback.substring(callback.length() - 1))) {
                            callback = callback + encodedText;
                        } else if (callback.contains("?")){
                            callback = callback + "&text=" + encodedText;
                        } else {
                            callback = callback + "?text=" + encodedText;
                        }
                        sendIntent.setData(Uri.parse(callback));
                        startActivity(sendIntent);
                    }
                } else {
                    Toast.makeText(ScanQRCodeActivity.this, R.string.wifi, Toast.LENGTH_SHORT).show();
                    String ssid = text.split("S:")[1].split(";T:")[0].split(";P:")[0];
                    String password = text.split("P:")[1].split(";T:")[0].split(";S:")[0];
                    //Intent i = new Intent(ACTION_WIFI_ADD_NETWORKS);
                }
            } catch (ActivityNotFoundException activityNotFound) {
                Toast.makeText(ScanQRCodeActivity.this, R.string.activityNotFound, Toast.LENGTH_SHORT).show();
            }
            mCodeScanner.startPreview();
            dialog.cancel();
        });
        builder.setNeutralButton(getString(R.string.generate), (dialog, which) -> {
            Intent gotoSecond = new Intent(ScanQRCodeActivity.this, CreateQRCodeActivity.class);
            gotoSecond.putExtra("text", text);
            startActivity(gotoSecond);
        });
        builder.setNegativeButton(R.string.alertDialogCancel, (dialog, id) -> {
            mCodeScanner.startPreview();
            dialog.cancel();
        });
        builder.show();
    }

    public boolean isCameraPermissionGranted() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            return false;
        }
    }
}