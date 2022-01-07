package com.example.postory.activities;

import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.dialogs.DelayedProgressDialog;
import com.example.postory.utils.PasswordController;
import com.example.postory.utils.StringListHandler;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Activity for users sign up operation.
 * Can be accessed from LoginActivity
 * Checks if the data of user is valid, with PasswordController class.
 * @author niyaziulke
 */
public class RegisterActivity extends AppCompatActivity {
    TextInputEditText name;
    TextInputEditText surname;
    TextInputEditText mail;
    TextInputEditText username;
    TextInputEditText password;
    TextInputEditText repeatPassword;
    Button signUpButton;
    DelayedProgressDialog dialog;

    /**
     * Triggered when the activity is first created, sets things up.
     * @param savedInstanceState The state of instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        mail = findViewById(R.id.mail);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        repeatPassword = findViewById(R.id.repeatPassword);
        signUpButton = findViewById(R.id.signUpButton);
        dialog = new DelayedProgressDialog();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordCheck()) {
                    sendSignUp();
                }

            }
        });
    }

    /**
     * @return if the password satisfy requirements.
     */
    protected boolean passwordCheck() {
        String passwordText = password.getText().toString();
        String repeatPasswordText = repeatPassword.getText().toString();
        if (!passwordText.equals(repeatPasswordText)) {
            // Warn the user that repeated password does not match.
            SuperActivityToast.create(RegisterActivity.this, new Style(), Style.TYPE_BUTTON)
                    .setProgressBarColor(Color.WHITE)
                    .setText("The passwords do not match.")
                    .setDuration(Style.DURATION_SHORT)
                    .setFrame(Style.FRAME_LOLLIPOP)
                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                    .setAnimations(Style.ANIMATIONS_POP).show();
            return false;
        } else {
            PasswordController passwordController = new PasswordController(passwordText);
            List<String> errors = passwordController.getErrors();
            if (errors.size() > 0) {
                String singleStringError = StringListHandler.listToSingleString(errors);
                // Dialog to show all the errors related to the password.
                AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                builder1.setTitle("Your password is not secure.");
                builder1.setMessage(singleStringError);
                builder1.setCancelable(true);
                builder1.setNegativeButton(
                        "OKAY",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog invalidAlert = builder1.create();
                invalidAlert.show();
                return false;
            }
            return true;
        }
    }

    /**
     * The method that sends the request for sign up operation.
     */
    protected void sendSignUp() {
        final OkHttpClient client = new OkHttpClient();
        String url = "http://3.67.83.253:8000/auth/users/";
        String nameString = name.getText().toString();
        String surnameString = surname.getText().toString();
        String mailString = mail.getText().toString();
        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();
        String repeatPasswordString = password.getText().toString();

        // Check if any required field is empty.
        if (nameString.equals("") || surnameString.equals("") || mailString.equals("") || usernameString.equals("")
                || passwordString.equals("") || repeatPasswordString.equals("")) {
            SuperActivityToast.create(RegisterActivity.this, new Style(), Style.TYPE_BUTTON)
                    .setProgressBarColor(Color.WHITE)
                    .setText("Fill in all the fields.")
                    .setDuration(Style.DURATION_SHORT)
                    .setFrame(Style.FRAME_LOLLIPOP)
                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                    .setAnimations(Style.ANIMATIONS_POP).show();

            return;
        }


        RequestBody requestBody = null;
        try {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("name", nameString)
                    .addFormDataPart("surname", surnameString)
                    .addFormDataPart("email", mailString)
                    .addFormDataPart("username", usernameString)
                    .addFormDataPart("password", passwordString)
                    .addFormDataPart("re_password", repeatPasswordString)
                    .build();

            final Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            // Send request for sign up.
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("RegisterActivity", "Sign up request unsuccessful");
                }


                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    ResponseBody signUpResponse = response.body();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}