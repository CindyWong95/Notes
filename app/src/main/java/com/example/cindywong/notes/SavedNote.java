package com.example.cindywong.notes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.*;

public class SavedNote extends AppCompatActivity {

    TextView tvNote, tvFileName;
    Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_note);

        tvNote=(TextView)findViewById(R.id.tvNote);
        tvFileName=(TextView)findViewById(R.id.tvFileName);
        btnHome=(Button)findViewById(R.id.btnHome);

        Intent intent = getIntent();
        String note = intent.getStringExtra("note");
        String file = intent.getStringExtra("fileChosen");

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvFileName.setText(file);
        tvNote.setText(note);
    }
}
