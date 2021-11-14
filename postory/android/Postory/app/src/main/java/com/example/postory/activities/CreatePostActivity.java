package com.example.postory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.postory.R;

public class CreatePostActivity extends AppCompatActivity {

    Button sendButton;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        sendButton = (Button) findViewById(R.id.send_button);
        context = this;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkNecessaryData()) {

                } else {
                    Toast.makeText(context, R.string.fill_necessary_warning, Toast.LENGTH_LONG);
                }
            }
        });
    }

    protected boolean checkNecessaryData() {

        return false;
    }
}