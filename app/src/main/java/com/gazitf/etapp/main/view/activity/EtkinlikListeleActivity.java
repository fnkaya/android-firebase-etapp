package com.gazitf.etapp.main.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.gazitf.etapp.R;
import com.gazitf.etapp.main.adapter.ActivityAdapter;
import com.gazitf.etapp.main.model.ActivityShowModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class EtkinlikListeleActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    // Vertbanında etkinlikler burada bir ArrayListte tutulacak
    ArrayList activity_list = new ArrayList<ActivityShowModel>();



    private ActivityAdapter recyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etkinlik_listele);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new ActivityAdapter(activity_list);



    }
}