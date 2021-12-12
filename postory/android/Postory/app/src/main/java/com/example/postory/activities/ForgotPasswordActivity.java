package com.example.postory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.postory.R;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button resetButton;
    Button cancelButton;
    EditText resetMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetButton = findViewById(R.id.resetButton);
        cancelButton = findViewById(R.id.cancelButton);
        resetMail = findViewById(R.id.resetMail);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetButtonClick();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelButtonClick();
            }
        });
    }
    private void resetButtonClick(){
        if(resetMail.getText().toString().contains("@")){

        }
        else{
            SuperActivityToast.create(ForgotPasswordActivity.this, new Style(), Style.TYPE_BUTTON)
                    .setProgressBarColor(Color.WHITE)
                    .setText("The email address is not valid.")
                    .setDuration(Style.DURATION_LONG)
                    .setFrame(Style.FRAME_LOLLIPOP)
                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                    .setAnimations(Style.ANIMATIONS_POP).show();
        }
    }
    private void cancelButtonClick(){
        onBackPressed();
    }
}