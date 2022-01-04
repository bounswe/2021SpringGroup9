package com.example.postory.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.postory.BuildConfig;
import com.example.postory.R;
import com.example.postory.adapters.PostAdapter;
import com.example.postory.models.Post;
import com.example.postory.models.UserModel;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SelfProfilePageActivity extends ToolbarActivity {
    private PostAdapter postAdapter;
    private Request requestPosts;
    private Request requestUserData;
    private UserModel thisUser;
    private Uri imageUri;
    private TextView name;
    private TextView surname;
    private TextView username;
    private TextView followedBy;
    private TextView following;
    private TextView numPosts;
    private ImageView profilePicture;
    private SwitchCompat privateSwitch;
    private Button followRequestsButton;

    private SharedPreferences sharedPreferences;
    private String userId;
    String accessToken;
    private ListView listView;
    public static final String TAG = "get_user_info";
    private Post[] posts;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static final int TAKE_PHOTO = 0;
    public static final int PICK_GALLERY = 1;
    private final int CAMERA_PERMISSION_REQUEST = 1000;

    @Override
    protected void goHomeClicked() {
        Intent intent = new Intent(SelfProfilePageActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void goCreatePostClicked() {
        Intent intent = new Intent(SelfProfilePageActivity.this, CreatePostActivity.class);
        intent.putExtra("goal", "create");
        startActivity(intent);
    }

    @Override
    protected void refreshClicked() {
        return;
    }

    @Override
    protected void goExploreClicked() {
        Intent intent = new Intent(SelfProfilePageActivity.this, ExploreActivity.class);
        startActivity(intent);
    }

    @Override
    protected void logoutClicked() {
        sharedPreferences = getSharedPreferences("MY_APP", MODE_PRIVATE);
        sharedPreferences.edit().remove("valid_until").apply();
        sharedPreferences.edit().remove("user_id").apply();
        sharedPreferences.edit().remove("access_token").apply();
        Intent i = new Intent(SelfProfilePageActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_profile_page);
        super.initToolbar();
        listView = (ListView) findViewById(R.id.list_posts);

        name = (TextView) findViewById(R.id.name);
        surname = (TextView) findViewById(R.id.surname);
        username = (TextView) findViewById(R.id.username);
        followedBy = (TextView) findViewById(R.id.followedBy);
        following = (TextView) findViewById(R.id.following);
        numPosts = (TextView) findViewById(R.id.numPosts);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        privateSwitch = (SwitchCompat) findViewById(R.id.privateSwitch);
        followRequestsButton = (Button) findViewById(R.id.followRequestButton);

        sharedPreferences = getSharedPreferences("MY_APP", MODE_PRIVATE);
        accessToken = sharedPreferences.getString("access_token", "");
        userId = sharedPreferences.getString("user_id", "");
        String url1 = BuildConfig.API_IP + "/user/get/" + userId;
        final OkHttpClient client = new OkHttpClient();
        requestUserData = new Request.Builder()
                .url(url1)
                .addHeader("Authorization", "JWT " + accessToken)
                .build();
        client.newCall(requestUserData).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("send_request_to_get_data", "onFailure: ");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("send_request_to_get_data", "onResponse: ");

                Gson gson = new Gson();
                String respStr = response.body().string();
                Log.d("send_request_to_get_data", respStr);

                thisUser = gson.fromJson(respStr, UserModel.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setUserFields();
                        privateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                privateSwitchController(b);
                            }
                        });
                    }
                });
            }
        });

        callForPosts();
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProfilePicture();
            }
        });

        followRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SelfProfilePageActivity.this,FollowRequestActivity.class);
                startActivity(i);
            }
        });



    }

    private void setProfilePicture() {
        verifyStoragePermissions(SelfProfilePageActivity.this);
        addImage();
    }

    private void callForPosts() {
        String url2 = BuildConfig.API_IP + "/post/all/user/" + userId;

        requestPosts = new Request.Builder()
                .url(url2)
                .addHeader("Authorization", "JWT " + accessToken)
                .build();

        callAllPosts();
    }


    private void setUserFields() {
        name.setText(thisUser.getName());
        surname.setText(thisUser.getSurname());
        username.setText(thisUser.getUsername());
        privateSwitch.setChecked(thisUser.isPrivate());
        followedBy.setText("" + thisUser.getFollowerUsers().size());
        following.setText("" + thisUser.getFollowedUsers().size());
        numPosts.setText("" + thisUser.getPosts().size());
        Glide
                .with(SelfProfilePageActivity.this)
                .load(thisUser.getUserPhoto())
                .placeholder(R.drawable.placeholder)
                .apply(new RequestOptions().override(400, 400))
                .centerCrop()
                .into(profilePicture);

    }


    private void callAllPosts() {
        final OkHttpClient client = new OkHttpClient();
        client.newCall(requestPosts).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.i(TAG, "onResponse: ");
                Gson gson = new Gson();
                posts = gson.fromJson(response.body().string(), Post[].class);
                Log.i(TAG, "onResponse: ");
                ArrayList<Post> arrayOfPosts = new ArrayList<Post>();

                for (Post post : posts) {
                    arrayOfPosts.add(post);

                }
                Collections.reverse(arrayOfPosts);
                postAdapter = new PostAdapter(SelfProfilePageActivity.this, arrayOfPosts);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(postAdapter);
                    }
                });
            }
        });
    }

    protected void addImage() {
        final CharSequence[] options = {"Take Photo with Camera", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SelfProfilePageActivity.this);
        builder.setTitle(R.string.choose_image_source);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo with Camera")) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                    }
                    Intent useCamera = new Intent("android.media.action.IMAGE_CAPTURE")
                            .putExtra("android.intent.extras.CAMERA_FACING", 1);
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

    private String saveImage(Bitmap image) {
        String path = null;
        String imageFileName = "JPEG_" + "FILE_NAME" + ".jpg";
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/POSTORY");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case TAKE_PHOTO:
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
                            profilePicture.setImageBitmap(rotateBitmap(selectedImage, orientation));
                            out.flush();
                            out.close();
                            String url1 = BuildConfig.API_IP + "/user/addPhoto";
                            final OkHttpClient client = new OkHttpClient();
                            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
                            MultipartBody.Builder builder = bodyBuilder.setType(MultipartBody.FORM);
                            RequestBody requestBody = builder.addFormDataPart("userPhoto", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file)).build();

                            Request requestPhotoUpload = new Request.Builder()
                                    .url(url1)
                                    .post(requestBody)
                                    .addHeader("Authorization", "JWT " + accessToken)
                                    .build();
                            client.newCall(requestPhotoUpload).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Log.i(TAG, "onFailure: ");
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                }
                            });

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                            profilePicture.setImageBitmap(rotateBitmap(selectedImage, orientation));
                            out.flush();
                            out.close();

                            String url1 = BuildConfig.API_IP + "/user/addPhoto";
                            final OkHttpClient client = new OkHttpClient();
                            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
                            MultipartBody.Builder builder = bodyBuilder.setType(MultipartBody.FORM);
                            RequestBody requestBody = builder.addFormDataPart("userPhoto", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file)).build();

                            Request requestPhotoUpload = new Request.Builder()
                                    .url(url1)
                                    .post(requestBody)
                                    .addHeader("Authorization", "JWT " + accessToken)
                                    .build();
                            client.newCall(requestPhotoUpload).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Log.i(TAG, "onFailure: ");
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                }
                            });

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(SelfProfilePageActivity.this, "Image is not selected", Toast.LENGTH_SHORT);
                    }
                    break;
            }

        }
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

    private void privateSwitchController(boolean isChecked) {
        String url = BuildConfig.API_IP + "/user/changeProfile";
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        MultipartBody.Builder builder = bodyBuilder.setType(MultipartBody.FORM);
        RequestBody body = RequestBody.create(null, new byte[]{});

        Request changeProfileRequest = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "JWT " + accessToken)
                .build();
        final OkHttpClient client = new OkHttpClient();

        client.newCall(changeProfileRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.i("SelfProfilePageActivity","Profile visibility changed");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SuperActivityToast.create(SelfProfilePageActivity.this, new Style(), Style.TYPE_BUTTON)
                                .setProgressBarColor(Color.WHITE)
                                .setText("Profile privacy option is changed.")
                                .setDuration(Style.DURATION_LONG)
                                .setFrame(Style.FRAME_LOLLIPOP)
                                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_BLUE))
                                .setAnimations(Style.ANIMATIONS_POP).show();
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void goProfileClicked() {
        return;
    }

    @Override
    protected void goActivitiesClicked() {
        Intent i = new Intent(SelfProfilePageActivity.this, ActivityStreamActivity.class);
        startActivity(i);
    }
}