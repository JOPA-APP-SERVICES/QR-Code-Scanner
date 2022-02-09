package de.jopa.qrcodescanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class CreateQRCodeActivity extends AppCompatActivity {
    Bitmap bitmap;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qrcode);

        imageView = findViewById(R.id.imageView);
        Intent intent = getIntent();
        String text;

        try {
            Uri uri = intent.getData();
            text = uri.getQueryParameter("text");
        } catch (NullPointerException e) {
            text = intent.getStringExtra("text");
        }

        if ("".equals(text)) {
            text = intent.getExtras().getString("android.intent.extra.TEXT");
        }
        if (text == null) {
            text = " ";
        }

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setOnClickListener(v -> saveImage());
    }

    private void saveImage() {
        try {
            File cachePath = new File(CreateQRCodeActivity.this.getCacheDir(), "images");
            FileOutputStream stream = new FileOutputStream(cachePath + "/qrcode.jpg"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(CreateQRCodeActivity.this.getCacheDir(), "images");
        File newFile = new File(imagePath, "qrcode.jpg");
        Uri contentUri = FileProvider.getUriForFile(CreateQRCodeActivity.this, "de.jopa.qrcodescanner.CreateQRCodeActivity", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.chooseApp)));
        }
    }
}