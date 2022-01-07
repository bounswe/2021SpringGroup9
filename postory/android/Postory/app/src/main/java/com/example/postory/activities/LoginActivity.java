package com.example.postory.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.dialogs.DelayedProgressDialog;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * The activity for users to sign in. It can be accessed directly at the beginning.
 * If the user has logged in during the last 23 hours, she can automatically sign in.
 * Otherwise she needs to write her email and password.
 * The activity checks whether the email and password is accurate by sending a request to the API.
 * If they are valid, the token of the user is stored.
 * Otherwise, it warns the user.
 * @author niyaziulke
 */
public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    TextInputEditText mail;
    TextInputEditText password;
    Button signInButton;
    Button signUpButton;
    DateFormat dateFormat;
    TextView forgotPasswordText;
    Handler handler;


    /**
     * Triggered when the activity is first created, sets things up.
     * @param savedInstanceState The state of instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        mail = (TextInputEditText) findViewById(R.id.mail);
        password = (TextInputEditText) findViewById(R.id.password);
        signInButton = (Button) findViewById(R.id.signInButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        forgotPasswordText = (TextView) findViewById(R.id.forgotPassword);
        handler = new Handler();
        sharedPreferences = getSharedPreferences("MY_APP",MODE_PRIVATE);
        try {
            // Check if the last login is at most 23 hours before.
            String validDateString = sharedPreferences.getString("valid_until","");
            Date validDate = dateFormat.parse(validDateString);
            if(validDate.compareTo(Calendar.getInstance().getTime())>0){

                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



        // Button for sign in onClick defined
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSignIn()) {
                    sendSignIn();
                }

            }
        });

        // Button for forgot password onClick defined
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        // Button for sign up onClick defined.
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * The method to check syntactic validity of user email and password at the sign in.
     * @return if the input email and password is syntactically correct.
     */
    protected boolean checkSignIn() {
        String mailString = mail.getText().toString();
        String passwordString = password.getText().toString();

        if (!mailString.contains("@")) {
            SuperActivityToast.create(LoginActivity.this, new Style(), Style.TYPE_BUTTON)
                    .setProgressBarColor(Color.WHITE)
                    .setText("Please enter a valid mail address.")
                    .setDuration(Style.DURATION_SHORT)
                    .setFrame(Style.FRAME_LOLLIPOP)
                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                    .setAnimations(Style.ANIMATIONS_POP).show();
            return false;
        }
        if (passwordString.trim().equals("")) {
            SuperActivityToast.create(LoginActivity.this, new Style(), Style.TYPE_BUTTON)
                    .setProgressBarColor(Color.WHITE)
                    .setText("Please write your password.")
                    .setDuration(Style.DURATION_SHORT)
                    .setFrame(Style.FRAME_LOLLIPOP)
                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                    .setAnimations(Style.ANIMATIONS_POP).show();
            return false;
        }

        return true;
    }


    /**
     * Sends the sign in request to the backend
     */
    protected void sendSignIn() {
        final OkHttpClient client = new OkHttpClient();
        String url = "http://3.67.83.253:8000/auth/jwt/create";
        String mailString = mail.getText().toString().trim();
        String passwordString = password.getText().toString();

        RequestBody requestBody = null;
        try {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", mailString)
                    .addFormDataPart("password", passwordString)
                    .build();
            final Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Log.d("loginactivity", "requestcreate");


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("loginactivity", "Sign in request unsuccessful");
                }


                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    ResponseBody signInResponse = response.body();
                    String signInResponseString = signInResponse.string();
                    SharedPreferences preferences = getSharedPreferences("MY_APP", Context.MODE_PRIVATE);
                    try {
                        JSONObject json = new JSONObject(signInResponseString);
                        String accessToken = json.getString("access");
                        // Save the access token in SharedPreferences
                        preferences.edit().putString("access_token", accessToken).apply();
                        JWT jwt = new JWT(accessToken);
                        // Get the user id
                        Claim userIdClaim = jwt.getClaim("user_id");

                        String userId = userIdClaim.asString();
                        preferences.edit().putString("user_id", userId).apply();

                        Date endTime = Calendar.getInstance().getTime();

                        final long hour = 3600000; // an hour
                        final long hours_23 = 23*hour; //23 hours
                        endTime.setTime(endTime.getTime()+hours_23);
                        String endDateString = dateFormat.format(endTime);

                        // Put the end time of validity of this login (23 hours)
                        preferences.edit().putString("valid_until",endDateString).apply();

                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("loginactivity", signInResponseString);


                }

            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}