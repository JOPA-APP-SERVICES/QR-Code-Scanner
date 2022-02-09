package de.jopa.qrcodescanner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ScanQRCodeFromFileActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode_from_file);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            Uri data = result.getData().getData();
                            String path = getPath(data);
                            InputStream is = null;
                            try {
                                is = new BufferedInputStream(new FileInputStream(path));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            send(scan(bitmap));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> pickImage());
    }

    public void pickImage() {
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }

    public static String scan(Bitmap bMap) {
        String contents = null;

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap);
            contents = result.getText();
        }
        catch (Exception e) {
            Log.e("QrTest", "Error decoding barcode", e);
        }
        return contents;
    }

    private void send(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRCodeFromFileActivity.this);
        builder.setTitle(R.string.app_name);
        TextView textView = new TextView(ScanQRCodeFromFileActivity.this);
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
                    }
                    startActivity(sendIntent);
                } else {
                    Toast.makeText(ScanQRCodeFromFileActivity.this, R.string.wifi, Toast.LENGTH_SHORT).show();
                    String ssid = text.split("S:")[1].split(";T:")[0].split(";P:")[0];
                    String password = text.split("P:")[1].split(";T:")[0].split(";S:")[0];
                    //Intent i = new Intent(ACTION_WIFI_ADD_NETWORKS);
                }
            } catch (ActivityNotFoundException activityNotFound) {
                Toast.makeText(ScanQRCodeFromFileActivity.this, R.string.activityNotFound, Toast.LENGTH_SHORT).show();
            }
            dialog.cancel();
        });
        builder.setNeutralButton(getString(R.string.generate), (dialog, which) -> {
            Intent gotoSecond = new Intent(ScanQRCodeFromFileActivity.this, CreateQRCodeActivity.class);
            gotoSecond.putExtra("text", text);
            startActivity(gotoSecond);
        });
        builder.setNegativeButton(R.string.alertDialogCancel, (dialog, id) -> dialog.cancel());
        builder.show();
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}