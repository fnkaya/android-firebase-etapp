package com.gazitf.etapp.create;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gazitf.etapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateActivity extends AppCompatActivity {
    Button btn_olustur;
    EditText txt_etkinlikAdi, txt_etkinlikAciklama, txt_kont, edt_baslangic, edt_bitis, edt_basSaati, edt_bitSaati, txt_adres;
    ImageView img_etkinlikYeri, img_basla, img_bitis, img_basSaati, img_bitSaati;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private int year, month, dayOfMonth;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        btn_olustur = findViewById(R.id.btn_olustur);
        txt_etkinlikAdi = findViewById(R.id.txt_etkinlikAdi);
        txt_etkinlikAciklama = findViewById(R.id.txt_etkinlikAciklama);
        txt_kont = findViewById(R.id.txt_kont);
        edt_baslangic = findViewById(R.id.edt_baslangic);
        edt_bitis = findViewById(R.id.edt_bitis);
        img_etkinlikYeri = findViewById(R.id.img_konum);
        img_basla = findViewById(R.id.img_basla); // tarih
        img_bitis = findViewById(R.id.img_bitis); // tarih
        img_basSaati = findViewById(R.id.img_basSaati);
        img_bitSaati = findViewById(R.id.img_bitSaati);
        edt_basSaati = findViewById(R.id.edt_basSaati);
        edt_bitSaati = findViewById(R.id.edt_bitSaati);
        txt_adres = findViewById(R.id.txt_adres);


 /*       findViewById(R.id.edt_baslangic).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                return false;
            }
        });*/

        img_basla.setOnClickListener(new View.OnClickListener() { // takvim
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(CreateActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                edt_baslangic.setText(day + "." + (month+1) + "." + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.setTitle("Tarih seçiniz");
                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,"SEÇ",datePickerDialog);
                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"İPTAL",datePickerDialog);
                datePickerDialog.show();
            }
        });

        img_basSaati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int saat = calendar.get(Calendar.HOUR);
                int dakika = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateActivity.this, AlertDialog.THEME_HOLO_DARK,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Date date = null;
                        DateFormat mformat = new SimpleDateFormat("HH:mm");
                        try {
                            date = mformat.parse(hourOfDay + ":" + minute);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        edt_basSaati.setText(mformat.format(date));
                    }
                },saat,dakika,true);
                timePickerDialog.setTitle("Saat Seçiniz");
                timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,"SEÇ",timePickerDialog);
                timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"İPTAL",timePickerDialog);
                timePickerDialog.show();
            }
        });

        img_bitis.setOnClickListener(new View.OnClickListener() { // takvim
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(CreateActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                edt_bitis.setText(day + "." + (month+1) + "." + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.setTitle("Tarih seçiniz");
                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,"SEÇ",datePickerDialog);
                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"İPTAL",datePickerDialog);
                datePickerDialog.show();
            }
        });
/*
        img_etkinlikYeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"MErhaba",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CreateActivity.this,MapsActivity.class);
                startActivity(intent);
                intent.putExtra("info","new");
            }
        });*/

        btn_olustur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateActivity.this,"Merhaba",Toast.LENGTH_LONG).show();
                String etkinlikAdi = txt_etkinlikAdi.getText().toString();
                String etkinlikAciklama = txt_etkinlikAciklama.getText().toString();
                int kontenjan = Integer.parseInt(txt_kont.getText().toString());
                String baslamaZamani = edt_baslangic.getText().toString();
                String bitmeZamani = edt_bitis.getText().toString();
                String adres = txt_adres.getText().toString();


                //Toast.makeText(getApplicationContext(),baslamaZamani + " " + bitmeZamani,Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),"Merhaba",Toast.LENGTH_LONG).show();

            }
        });

    }
}
