package com.example.phonecalls;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private EditText phoneNumberEditText;
    private Button callButton;
    private ImageButton deleteButton;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    makePhoneCall();
                } else {
                    Toast.makeText(this, "Permission to make phone calls was denied.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        callButton = findViewById(R.id.callButton);
        deleteButton = findViewById(R.id.deleteButton);

        phoneNumberEditText.setShowSoftInputOnFocus(false);

        callButton.setOnClickListener(view -> checkPermissionAndCall());

        deleteButton.setOnClickListener(view -> {
            String currentNumber = phoneNumberEditText.getText().toString();
            if (!currentNumber.isEmpty()) {
                phoneNumberEditText.setText(currentNumber.substring(0, currentNumber.length() - 1));
                phoneNumberEditText.setSelection(phoneNumberEditText.getText().length());
            }
        });
    }

    public void onDialerClick(View view) {
        Button button = (Button) view;
        String digit = button.getText().toString();
        phoneNumberEditText.append(digit);
    }

    private void checkPermissionAndCall() {
        String phoneNumber = phoneNumberEditText.getText().toString();
        if (phoneNumber.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            makePhoneCall();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
        }
    }

    private void makePhoneCall() {
        String phoneNumber = phoneNumberEditText.getText().toString();
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + phoneNumber));

        try {
            startActivity(phoneIntent);
            // --- REMOVED: The call to showHangUpUI(true) ---
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission error. Please grant call permission in settings.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
