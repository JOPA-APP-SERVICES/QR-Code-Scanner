package de.jopa.qrcodescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.github.kaiwinter.androidremotenotifications.RemoteNotifications;
import com.github.kaiwinter.androidremotenotifications.model.UpdatePolicy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    LinearLayout input;
    Spinner type;
    EditText text;
    EditText email1;
    EditText email2;
    EditText email3;
    EditText password;
    EditText number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);
        try {
            RemoteNotifications.start(MainActivity.this, new URL("https://jopaapi.web.app/qr/notifications.json"), UpdatePolicy.NOW);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            if ("/qr/scan".equals(getIntent().getData().getPath())) {
                startScanner(getIntent().getData().getQueryParameter("callback"));
            }
        } catch (NullPointerException e) {
            //pass
        }

        Button button = findViewById(R.id.button);
        fab = findViewById(R.id.fab);

        button.setOnClickListener(v -> startScanner(null));
        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.alertDialogText2);

            String[] types = {"Text/URL", "Email", "WiFi", "WhatsApp"};
            final ArrayAdapter<String> adp = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_spinner_item, types);

            final Spinner sp = new Spinner(MainActivity.this);
            sp.setAdapter(adp);

            builder.setView(sp);

            // Set up the buttons
            builder.setPositiveButton(getString(R.string.alertDialogOK), (dialog, which) -> {
                int item = sp.getSelectedItemPosition();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle(sp.getSelectedItem().toString());
                builder1.setMessage(getString(R.string.alertDialogText));

                switch (item) {
                    case 0: {
                        // Set up the input
                        text = new EditText(MainActivity.this);
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                        text.setHint("Text/URL");
                        input = new LinearLayout(MainActivity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        input.setLayoutParams(params);
                        input.setOrientation(LinearLayout.VERTICAL);
                        input.addView(text);
                        break;
                    }
                    case 1: {
                        email1 = new EditText(MainActivity.this);
                        email2 = new EditText(MainActivity.this);
                        email3 = new EditText(MainActivity.this);
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        email1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        email2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
                        email3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
                        email1.setHint("Email");
                        email2.setHint(R.string.subject);
                        email3.setHint(R.string.body);
                        input = new LinearLayout(MainActivity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        input.setLayoutParams(params);
                        input.setOrientation(LinearLayout.VERTICAL);
                        input.addView(email1);
                        input.addView(email2);
                        input.addView(email3);
                        break;
                    }
                    case 2: {
                        String[] types1 = {"WPA", "WEP", "nyanpass"};
                        final ArrayAdapter<String> adp1 = new ArrayAdapter<>(MainActivity.this,
                                android.R.layout.simple_spinner_item, types1);

                        type = new Spinner(MainActivity.this);
                        type.setAdapter(adp1);
                        text = new EditText(MainActivity.this);
                        password = new EditText(MainActivity.this);
                        text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        text.setHint("SSID");
                        password.setHint(R.string.password);
                        input = new LinearLayout(MainActivity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        input.setLayoutParams(params);
                        input.setOrientation(LinearLayout.VERTICAL);
                        input.addView(type);
                        input.addView(text);
                        input.addView(password);
                        break;
                    }
                    case 3: {
                        number = new EditText(MainActivity.this);
                        text = new EditText(MainActivity.this);
                        number.setInputType(InputType.TYPE_CLASS_PHONE);
                        text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                        number.setHint(R.string.number);
                        text.setHint("Text");
                        input = new LinearLayout(MainActivity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        input.setLayoutParams(params);
                        input.setOrientation(LinearLayout.VERTICAL);
                        input.addView(number);
                        input.addView(text);
                        break;
                    }
                }
                builder1.setView(input);

                // Set up the buttons
                builder1.setPositiveButton(getString(R.string.alertDialogOK), new DialogInterface.OnClickListener() {
                    String value;
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (item) {
                            case 0:
                                value = text.getText().toString();
                                break;
                            case 1:
                                value = "mailto:" + email1.getText().toString() + "?subject=" + email2.getText().toString() + "&body=" + email3.getText().toString();
                                break;
                            case 2:
                                value = "WIFI:T:" + type.getSelectedItem().toString() + ";S:" + text.getText().toString() + ";P:" + password.getText().toString() + ";;";
                                break;
                            case 3:
                                value = "https://wa.me/" + number.getText().toString() + "/?text=" + text.getText().toString();
                                break;
                        }
                        Intent gotoSecond = new Intent(MainActivity.this, CreateQRCodeActivity.class);
                        gotoSecond.putExtra("text", value);
                        startActivity(gotoSecond);
                    }
                });
                builder1.setNegativeButton(getString(R.string.alertDialogCancel), (dialog1, which1) -> dialog1.cancel());

                builder1.show();
            });
            builder.show();
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        AlertDialog.Builder builder;
        AlertDialog dialog;
        switch (id){
            case R.id.licences:
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("code-scanner - yuriy-budiyev\n" +
                        "zxing-android-embedded - journeyapps\nzxing-core - Google\nandroid-remote-notifications - kaiwinter").setTitle(R.string.app_name);
                builder.setNegativeButton(R.string.alertDialogCancel, (dialog12, id12) -> dialog12.cancel());
                dialog = builder.create();
                dialog.show();
                return true;
            case R.id.about:
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.aboutText).setTitle(R.string.app_name);
                builder.setNegativeButton(R.string.alertDialogCancel, (dialog1, id1) -> dialog1.cancel());
                dialog = builder.create();
                dialog.show();
                return true;
            case R.id.help:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("https://jopaapps.web.app/apps/qr-code-scanner"));
                startActivity(sendIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void grantCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }
    public void grantStoragePermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
    public void startScanner(String callback) {
        grantCameraPermission();
        grantStoragePermission();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.scanQRCode));
        builder.setMessage(getString(R.string.scanType));
        String[] types = {getString(R.string.camera), getString(R.string.image)};
        final ArrayAdapter<String> adp = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_item, types);

        final Spinner sp = new Spinner(MainActivity.this);
        sp.setAdapter(adp);

        builder.setView(sp);
        builder.setPositiveButton(getString(R.string.alertDialogOK), (dialogInterface, i) -> {
            if (sp.getSelectedItemPosition() == 0) {
                Intent gotoSecond = new Intent(MainActivity.this, ScanQRCodeActivity.class);
                gotoSecond.putExtra("callback", callback);
                startActivity(gotoSecond);
            } else {
                Intent gotoSecond = new Intent(MainActivity.this, ScanQRCodeFromFileActivity.class);
                gotoSecond.putExtra("callback", callback);
                startActivity(gotoSecond);
            }
        });
        builder.setNegativeButton(getString(R.string.alertDialogCancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }
}