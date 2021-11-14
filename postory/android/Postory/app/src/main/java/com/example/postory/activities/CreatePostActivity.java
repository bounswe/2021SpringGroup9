package com.example.postory.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.postory.R;

public class CreatePostActivity extends AppCompatActivity {

    Button sendButton;
    EditText nicknameEditText;
    EditText titleEditText;
    EditText storyEditText;
    ImageView postImage;

    public static final int TAKE_PHOTO = 0;
    public static final int PICK_GALLERY = 1;
    private final int CAMERA_PERMISSION_REQUEST = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        sendButton = (Button) findViewById(R.id.send_button);
        nicknameEditText = (EditText) findViewById(R.id.op_name_field);
        titleEditText = (EditText) findViewById(R.id.op_enter_title);
        storyEditText = (EditText) findViewById(R.id.post_story_text_field);
        postImage = (ImageView) findViewById(R.id.post_photo);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkNecessaryData()) {
                    //TODO: use the data
                } else {
                    Toast.makeText(CreatePostActivity.this, R.string.fill_necessary_warning, Toast.LENGTH_LONG).show();
                }
            }
        });
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });
    }

    protected void addImage(){
        final CharSequence[] options = {"Take Photo with Camera", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
        builder.setTitle(R.string.choose_image_source);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo with Camera")) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                    }
                    Intent useCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(useCamera, TAKE_PHOTO);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent useGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(useGallery, PICK_GALLERY);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    if (resultCode == RESULT_OK && data != null) {
                        //TODO: use image from camera
                    }

                    break;
                case PICK_GALLERY:
                    if (resultCode == RESULT_OK && data != null) {
                        //TODO: use image from gallery
                    }
                    break;
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


    protected boolean checkNecessaryData() {
        return false;
    }

}