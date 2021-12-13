package com.example.postory.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        String mailString = resetMail.getText().toString();
        if(resetMail.getText().toString().contains("@")){
            OkHttpClient client = new OkHttpClient();
            String url = "http://3.67.83.253:8000/auth/users/reset_password";
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
            MultipartBody.Builder builder = bodyBuilder.setType(MultipartBody.FORM);
            RequestBody requestBody = builder.addFormDataPart("email",mailString).build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d("forgotpasswordActivity", "onFailure: ");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.d("forgotpasswordActivity", "onResponse");

                    ForgotPasswordActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ForgotPasswordActivity.this);
                            builder1.setTitle("Password reset link has been sent.");
                            builder1.setMessage("Please use the link to reset your password.");
                            builder1.setNegativeButton(
                                    "OKAY",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            onBackPressed();
                                        }
                                    });

                            AlertDialog invalidAlert = builder1.create();
                            invalidAlert.show();

                        }
                    });
                    }
                });

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