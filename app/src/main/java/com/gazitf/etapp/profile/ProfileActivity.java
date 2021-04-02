package com.gazitf.etapp.profile;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gazitf.etapp.R;
import com.gazitf.etapp.auth.activity.SplashActivity;
import com.gazitf.etapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.cert.PolicyNode;
import java.util.HashMap;
import java.util.zip.Inflater;

public class ProfileActivity extends AppCompatActivity {

    TextView username, email, phone;
    ImageView image;
    Button buton;
    FloatingActionButton namebuton,emailbuton,phonebuton;
    static final int SELECT_IMAGE = 1;


    private Uri uri;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        image = findViewById(R.id.avatarIv);
        buton = findViewById(R.id.buton);
        namebuton=findViewById(R.id.button_name);


        namebuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iintent=new Intent(getApplicationContext(),UpdateNameActivity.class);
                startActivity(iintent);
            }
        });






        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_IMAGE);


            }
        });


        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();


        username.setText(user.getDisplayName());
        email.setText(user.getEmail());
        phone.setText(user.getPhoneNumber());


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            uri = data.getData();
            image.setImageURI(uri);
        }
        else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Resim seçme iptal edildi", Toast.LENGTH_LONG).show();
            }

        }


    }

