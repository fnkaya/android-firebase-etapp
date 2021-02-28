package com.gazitf.etapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gazitf.etapp.giris.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        ImageView imageViewProfile = findViewById(R.id.image_view_profile);
        TextView textUser = findViewById(R.id.text_user);
        Button btnSignOut = findViewById(R.id.button_sign_out);

        Picasso.get()
                .load(auth.getCurrentUser().getPhotoUrl())
                .into(imageViewProfile);

        textUser.setText(auth.getCurrentUser().getDisplayName() + " " + auth.getCurrentUser().getEmail() + " " + auth.getCurrentUser().getUid());

        btnSignOut.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            this.finish();
        });
    }


}