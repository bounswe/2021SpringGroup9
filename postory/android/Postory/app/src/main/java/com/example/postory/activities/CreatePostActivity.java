package com.example.postory.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import androidx.core.app.ActivityCompat;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.dialogs.DelayedProgressDialog;
import com.example.postory.fragments.MapFragment;
import com.example.postory.fragments.TimeChooserFragment;
import com.example.postory.models.LocationModel;
import com.example.postory.models.Post;
import com.example.postory.models.PostModel;
import com.example.postory.utils.TimeController;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import okhttp3.*;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.*;
import java.lang.reflect.Array;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CreatePostActivity extends ToolbarActivity {


    TimeController t;

    public TimeController getT() {
        return t;
    }

    public void setT(TimeController t) {
        this.t = t;
    }

    boolean isFirstTime = true;
    Button sendButton;
    Handler handler;
    EditText nicknameEditText;
    EditText titleEditText;
    EditText storyEditText;
    EditText dateEditText;
    EditText tagEditText;
    public EditText locationEditText;
    TextView title;
    ImageView postImage;
    ImageView timeChoose;
    AlertDialog alertDialog;
    Post post;
    ArrayList<LocationModel> locations;
    ImageView locationChoose;
    public LinearLayout stdLayout;
    public FrameLayout mapContainer;

    boolean isScrollable = true;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    String locationName;

    DelayedProgressDialog dialog;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String TAG = "CreatePostActivity";

    public static final int TAKE_PHOTO = 0;
    public static final int PICK_GALLERY = 1;
    private final int CAMERA_PERMISSION_REQUEST = 1000;

    private Uri imageUri;
    private String from;
    private SimpleDateFormat formatFromString;
    private SimpleDateFormat formatToDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new DelayedProgressDialog();
        setContentView(R.layout.activity_create_post);
        super.initToolbar();
        formatFromString = new SimpleDateFormat("DD/mm/yyyy");
        formatToDate = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        handler = new Handler();


        locations = new ArrayList<>();
        timeChoose = (ImageView) findViewById(R.id.choose_time);



        stdLayout = (LinearLayout) findViewById(R.id.create_post_std_layout);
        mapContainer = (FrameLayout) findViewById(R.id.frame_placeholder);

        final EditText editText = new EditText(this);



        locationChoose = (ImageView) findViewById(R.id.btn_location_choose);
        sendButton = (Button) findViewById(R.id.send_button);
        nicknameEditText = (EditText) findViewById(R.id.op_name_field);
        tagEditText = (EditText) findViewById(R.id.op_tag_field);
        titleEditText = (EditText) findViewById(R.id.op_enter_title);
        title = (TextView) findViewById(R.id.create_new_post);
        storyEditText = (EditText) findViewById(R.id.post_story_text_field);
        dateEditText = (EditText) findViewById(R.id.op_date_text);
        locationEditText = (EditText) findViewById(R.id.op_location_text);

        from = getIntent().getStringExtra("goal");

        if (from.equals("edit")) {
            title.setText("Edit The Post");

            final OkHttpClient client = new OkHttpClient();
            String url = BuildConfig.API_IP + "/post/get/" + getIntent().getStringExtra("id");
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            dialog.show(getSupportFragmentManager(), "ASDASD");

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.i(TAG, "onFailure: ");
                    dialog.cancel();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    dialog.cancel();
                    Log.i(TAG, "onResponse: ");
                    Gson gson = new Gson();
                    post = gson.fromJson(response.body().string(), Post.class);
                    Log.i(TAG, "onResponse: ");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (post.getImages().size() != 0) {
                                Glide
                                        .with(CreatePostActivity.this)
                                        .load(post.getImages().get(0))
                                        .placeholder(R.drawable.placeholder)
                                        .centerCrop()
                                        .into(postImage);
                            }

                            titleEditText.setText(post.getTitle());
                            nicknameEditText.setText(post.getOwner());
                            if (post.getLocations().size() != 0) {
                                locationEditText.setText((String) post.getLocations().get(0).get(0));
                            }
                            if (post.getTags().size() != 0) {
                                tagEditText.setText(post.getTags().get(0));
                            }
                            dateEditText.setText(new SimpleDateFormat("dd/MM/yyyy").format(post.getStoryDate()));
                            storyEditText.setText(post.getStory());

                        }
                    });


                }
            });
        } else {
            title.setText("Create New Post");
        }

        verifyStoragePermissions(this);

        postImage = (ImageView) findViewById(R.id.post_photo);

        timeChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stdLayout.setVisibility(View.GONE);
                mapContainer.setVisibility(View.VISIBLE);
                TimeChooserFragment tf = new TimeChooserFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_placeholder,tf)
                        .commit();

            }
        });



        locationChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stdLayout.setVisibility(View.GONE);
                mapContainer.setVisibility(View.VISIBLE);


                if(isFirstTime) {
                    SuperActivityToast.create(CreatePostActivity.this, new Style(), Style.TYPE_BUTTON)
                            .setProgressBarColor(Color.WHITE)
                            .setText("In order to pin a location on the map, long click a location on the map and give it a name!")
                            .setDuration(Style.DURATION_LONG)
                            .setFrame(Style.FRAME_LOLLIPOP)
                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_BLUE))
                            .setAnimations(Style.ANIMATIONS_POP).show();
                    isFirstTime = false;
                }


                MapFragment mf = new MapFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_placeholder,mf)
                        .commit();

            }
        });





        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (from.equals("edit")) {


                    if (checkNecessaryData()) {
                        File file;

                        if (imageUri == null) {
                            BitmapDrawable drawable = (BitmapDrawable) postImage.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();
                            Log.i(TAG, "onClick: ");
                            String path = saveImage(bitmap);
                            file = new File(path);

                        } else {

                            file = new File(getRealPathFromURI(imageUri));

                        }

                        OkHttpClient client = new OkHttpClient();


                        String url = BuildConfig.API_IP + "/post/put/" + getIntent().getStringExtra("id");
                        Request request = buildEditCreateRequest(url, file);
                        dialog.show(getSupportFragmentManager(), "Post is being edited...");

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                dialog.cancel();
                                Log.i(TAG, "onFailure: ");
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                Log.i(TAG, "onResponse: ");
                                dialog.cancel();
                                CreatePostActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SuperActivityToast.create(CreatePostActivity.this, new Style(), Style.TYPE_BUTTON)
                                                .setProgressBarColor(Color.WHITE)
                                                .setText("Edited Succesfully!")
                                                .setDuration(Style.DURATION_LONG)
                                                .setFrame(Style.FRAME_LOLLIPOP)
                                                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_BLUE))
                                                .setAnimations(Style.ANIMATIONS_POP).show();

                                    }
                                });

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CreatePostActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);
                                                startActivity(intent);

                                            }
                                        });

                                    }
                                }, 2000);

                            }
                        });


                    } else {
                        SuperActivityToast.create(CreatePostActivity.this, new Style(), Style.TYPE_BUTTON)
                                .setProgressBarColor(Color.WHITE)
                                .setText(String.valueOf(R.string.fill_necessary_warning))
                                .setDuration(Style.DURATION_LONG)
                                .setFrame(Style.FRAME_LOLLIPOP)
                                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                                .setAnimations(Style.ANIMATIONS_POP).show();

                    }

                } else {
                    if (checkNecessaryData()) {

                        File file = new File(getRealPathFromURI(imageUri));
                        OkHttpClient client = new OkHttpClient();
                        String url = BuildConfig.API_IP + "/post/create";


                        dialog.show(getSupportFragmentManager(), "Post is being created...");
                        Request request = buildEditCreateRequest(url, file);

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
                                CreatePostActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SuperActivityToast.create(CreatePostActivity.this, new Style(), Style.TYPE_BUTTON)
                                                .setProgressBarColor(Color.WHITE)
                                                .setText("Post Created Successfully!")
                                                .setDuration(Style.DURATION_LONG)
                                                .setFrame(Style.FRAME_LOLLIPOP)
                                                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_BLUE))
                                                .setAnimations(Style.ANIMATIONS_POP).show();

                                    }
                                });

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CreatePostActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);
                                                startActivity(intent);

                                            }
                                        });

                                    }
                                }, 2000);

                            }
                        });
                    } else {
                        Toast.makeText(CreatePostActivity.this, R.string.fill_necessary_warning, Toast.LENGTH_LONG).show();
                    }

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

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    private Request buildEditCreateRequest(String url, File file) {
        RequestBody requestBody = null;


        int [] years = new int[2];
        int [] months = new int[2];
        int [] days = new int[2];
        int [] hours = new int[2];
        int [] minutes = new int[2];

        int precision = t.getPrecision();
        switch (precision){
            case 4:
                years[0] = t.getStartYear();
                years[1] = t.getEndYear();
                months[0] = t.getStartMonth();
                months[1] = t.getEndMonth();
                days[0] = t.getStartDay();
                days[1] = t.getEndDay();
                hours[0] = t.getStartHour();
                hours[1] = t.getEndHour();
                minutes[0] = t.getStartMinute();
                minutes[1] = t.getEndMinute();
                break;
            case 3:
                years[0] = t.getStartYear();
                years[1] = t.getEndYear();
                months[0] = t.getStartMonth();
                months[1] = t.getEndMonth();
                days[0] = t.getStartDay();
                days[1] = t.getEndDay();
                break;
            case 2:
                years[0] = t.getStartYear();
                years[1] = t.getEndYear();
                months[0] = t.getStartMonth();
                months[1] = t.getEndMonth();

                break;
            case 1 :
                years[0] = t.getStartYear();
                years[1] = t.getEndYear();
                break;
        }

        try {



            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
            MultipartBody.Builder builder = bodyBuilder.setType(MultipartBody.FORM);



            Gson gson = new Gson();

            for(LocationModel loc : locations) {
                String key = gson.toJson(loc);
                builder.addFormDataPart("locations", key );

            }


            requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("title", titleEditText.getText().toString())
                    .addFormDataPart("story", storyEditText.getText().toString())
                    .addFormDataPart("owner", nicknameEditText.getText().toString())
                    .addFormDataPart("locations", locationEditText.getText().toString())
                    .addFormDataPart("storyDate", formatToDate.format(formatFromString.parse(dateEditText.getText().toString())))
                    .addFormDataPart("tags", tagEditText.getText().toString())
                    .addFormDataPart("images", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                    .build();



        } catch (ParseException e) {
            SuperActivityToast.create(CreatePostActivity.this, new Style(), Style.TYPE_BUTTON)
                    .setProgressBarColor(Color.WHITE)
                    .setText("WRONG FORM OF DATE (Should be in the form DD/mm/yyyy )!")
                    .setDuration(Style.DURATION_LONG)
                    .setFrame(Style.FRAME_LOLLIPOP)
                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                    .setAnimations(Style.ANIMATIONS_POP).show();
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        return request;
    }

    private String saveImage(Bitmap image) {
        String path = null;
        String imageFileName = "JPEG_" + "FILE_NAME" + ".jpg";
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/YOUR_FOLDER_NAME");
        boolean success = true;
        if (!dir.exists()) {
            success = dir.mkdirs();
        }
        if (success) {
            File jpegFile = new File(dir, imageFileName);
            path = jpegFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(jpegFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return path;
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
                    /*
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                        }
                        Intent useCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(useCamera, TAKE_PHOTO);
                    */
                    SuperActivityToast.create(CreatePostActivity.this, new Style(), Style.TYPE_BUTTON)
                            .setProgressBarColor(Color.WHITE)
                            .setText("This feature is not available now.")
                            .setDuration(Style.DURATION_LONG)
                            .setFrame(Style.FRAME_LOLLIPOP)
                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                            .setAnimations(Style.ANIMATIONS_POP).show();


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

    public void  addLocation(LocationModel model) {
        locations.add(model);
        Log.i(TAG, "addLocation: ");
    };


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
                            postImage.setImageBitmap(rotateBitmap(selectedImage, orientation));
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
        } else if (orientation == 3) {
            matrix.postRotate(180);
        } else if (orientation == 8) {
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

    @Override
    protected void refreshClicked() {
        return;
    }

    @Override
    protected void goCreatePostClicked() {
        return;
    }

    @Override
    protected void goExploreClicked() {
        SuperActivityToast.create(CreatePostActivity.this, new Style(), Style.TYPE_BUTTON)
                .setProgressBarColor(Color.WHITE)
                .setText("This feature is not available now.")
                .setDuration(Style.DURATION_LONG)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                .setAnimations(Style.ANIMATIONS_POP).show();
    }

    @Override
    protected void goHomeClicked() {
        Intent i = new Intent(CreatePostActivity.this, MainActivity.class);
        finish();
        startActivity(i);
    }


}