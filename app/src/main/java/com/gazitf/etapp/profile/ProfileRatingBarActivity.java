package com.gazitf.etapp.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.gazitf.etapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileRatingBarActivity extends AppCompatActivity {

    RatingBar ratingBar;
    Button btn_submit;
    private CircleImageView imageViewProfilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_rating_bar);

        ratingBar=findViewById(R.id.rating_bar);
        btn_submit=findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=String.valueOf(ratingBar.getRating());
                Toast.makeText(getApplicationContext(),s+"Star"
                ,Toast.LENGTH_SHORT).show();
            }
        });
    }
}