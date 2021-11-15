package com.example.postory.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Matrix;
import android.media.ExifInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import com.example.postory.R;
import com.example.postory.dialogs.DelayedProgressDialog;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CreatePostActivity extends AppCompatActivity {

    Button sendButton;
    EditText nicknameEditText;
    EditText titleEditText;
    EditText storyEditText;
    EditText dateEditText;
    EditText locationEditText;
    ImageView postImage;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String TAG = "CreatePostActivity";

    public static final int TAKE_PHOTO = 0;
    public static final int PICK_GALLERY = 1;
    private final int CAMERA_PERMISSION_REQUEST = 1000;

    public Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        ImageView homeButton = (ImageView) toolbar.findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sendButton = (Button) findViewById(R.id.send_button);
        nicknameEditText = (EditText) findViewById(R.id.op_name_field);
        titleEditText = (EditText) findViewById(R.id.op_enter_title);
        storyEditText = (EditText) findViewById(R.id.post_story_text_field);
        dateEditText = (EditText) findViewById(R.id.op_date_text);
        locationEditText = (EditText) findViewById(R.id.op_location_text);

        verifyStoragePermissions(this);

        postImage = (ImageView) findViewById(R.id.post_photo);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkNecessaryData()) {
                    //TODO: use the filled data
                    File file = new File(getRealPathFromURI(imageUri));
                    OkHttpClient client = new OkHttpClient();
                    String url = "http://35.158.95.81:8000/api/post/create";
                    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("title",titleEditText.getText().toString())
                            .addFormDataPart("story",storyEditText.getText().toString())
                            .addFormDataPart("owner",nicknameEditText.getText().toString())
                            .addFormDataPart("locations",locationEditText.getText().toString())
                            .addFormDataPart("storyDate",dateEditText.getText().toString())
                            .addFormDataPart("tags","fun")
                            .addFormDataPart("images",file.getName(),RequestBody.create(MediaType.parse("image/jpeg"),file))
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    final DelayedProgressDialog dialog = new DelayedProgressDialog();
                    dialog.show(getSupportFragmentManager(),"Post is being created...");

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.i(TAG, "onFailure: ");
                            dialog.cancel();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Log.i(TAG, "onResponse: ");
                            dialog.cancel();
                        }
                    });
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

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    protected void addImage() {
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
                        imageUri = data.getData();
                        final InputStream imageStream;
                        try {
                            imageStream = getContentResolver().openInputStream(imageUri);
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                            String fileName = "Image_post.jpg";
                            File file = new File(storageDir, fileName);
                            FileOutputStream out = new FileOutputStream(file);
                            selectedImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            ExifInterface exif = new ExifInterface(new File(getRealPathFromURI(imageUri)));
                            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                            postImage.setImageBitmap(rotateBitmap(selectedImage,orientation));
                            out.flush();
                            out.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(CreatePostActivity.this, "Image is not selected", Toast.LENGTH_SHORT);
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    protected boolean checkNecessaryData() {
        if (nicknameEditText.getText().toString().trim().equals("")) {
            return false;
        }
        if (titleEditText.getText().toString().trim().equals("")) {
            return false;
        }
        if (storyEditText.getText().toString().trim().equals("")) {
            return false;
        }
        if (dateEditText.getText().toString().trim().equals("")) {
            return false;
        }
        if (locationEditText.getText().toString().trim().equals("")) {
            return false;
        }
        return true;
    }

    protected Bitmap rotateBitmap(Bitmap bm, int orientation) {
        Matrix matrix = new Matrix();

        if (orientation == 6) {
            matrix.postRotate(90);
        }
        else if (orientation == 3) {
            matrix.postRotate(180);
        }
        else if (orientation == 8) {
            matrix.postRotate(270);
        }

        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}