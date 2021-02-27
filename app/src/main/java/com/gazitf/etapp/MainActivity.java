package com.gazitf.etapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        TextView textUser = findViewById(R.id.text_user);

        textUser.setText(auth.getCurrentUser().getDisplayName() + " " + auth.getCurrentUser().getEmail() + " " + auth.getCurrentUser().getPhoneNumber());

        Button btnSignOut = findViewById(R.id.button_sign_out);

        btnSignOut.setOnClickListener(view -> {
            auth.signOut();
            this.finish();
        });
    }


}