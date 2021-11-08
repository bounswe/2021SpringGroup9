package com.example.postory.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.postory.R;
import com.example.postory.adapters.PostAdapter;
import com.example.postory.models.PostModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_view_posts);




        ArrayList<PostModel> arrayOfPosts = new ArrayList<PostModel>();
        Bitmap bmParis = BitmapFactory.decodeResource(getResources(),R.drawable.paris);
        Bitmap bmRock = BitmapFactory.decodeResource(getResources(),R.drawable.rock_pushing);
        Bitmap bmKeke = BitmapFactory.decodeResource(getResources(),R.drawable.post_picture_example);
        String a = toBase64(bmParis);
        String a1 = toBase64(bmRock);
        String a2 = toBase64(bmKeke);

        String post1 = "Proin et erat est. Nunc mollis mi ipsum, a suscipit ligula tempus ut. Aenean non dui ac mi elementum hendrerit in eget orci. Aliquam tristique augue eros, vitae sodales turpis luctus ut. Aenean ex lectus, feugiat sit amet ipsum non, molestie fringilla sapien. Sed varius sit amet nisi id varius. Donec arcu turpis, efficitur vitae dolor et, rutrum fermentum leo. Donec vitae velit in tortor aliquam vulputate. Vivamus et ipsum mattis, fermentum mauris molestie, eleifend sem. Fusce vestibulum convallis ornare. Proin at sem risus. Proin congue ullamcorper libero in interdum.";
        String post2 = " Aliquam erat volutpat. Suspendisse potenti. Donec ornare enim ac est laoreet, at ullamcorper tortor laoreet. Pellentesque tincidunt ullamcorper vulputate. In hac habitasse platea dictumst. Nunc et quam ultrices, malesuada felis id, interdum eros. Sed vitae mauris posuere, tempor augue eu, viverra risus. Vivamus nunc eros, tristique vel purus ut, elementum hendrerit nisi. Aliquam mollis quam at mi consequat faucibus. Praesent eu aliquam nulla.";
        String post3 = "Duis et facilisis dui. Duis vel urna vel quam pharetra aliquam at at erat. Pellentesque at metus quis lacus gravida elementum. Pellentesque quis arcu aliquet, aliquet arcu vel, hendrerit tortor. Aenean non risus convallis, imperdiet ipsum nec, auctor ligula. Proin sed sem quis diam finibus porttitor. Nulla facilisi. In at nunc arcu. Suspendisse id lorem sit amet magna ullamcorper porta. Praesent quis placerat ipsum. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Maecenas tellus lacus, pharetra in mattis id, imperdiet ac nunc. Integer sit amet sapien risus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent sit amet eros vitae ligula tristique varius nec vel lectus.";



        PostModel model1 = new PostModel(a,a1,post1,"Ahmet Ertan");
        PostModel model2 = new PostModel(a1,a2,post2,"Sefa Ertay");
        PostModel model3 = new PostModel(a2,a,post3,"Kutlu Sever");





        PostAdapter postAdapter = new PostAdapter(this,arrayOfPosts);
        postAdapter.add(model1);
        postAdapter.add(model2);
        postAdapter.add(model3);

        listView.setAdapter(postAdapter);
    }



    private String toBase64(Bitmap bm) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm is the bitmap object
        byte[] b = baos.toByteArray();
        return  Base64.encodeToString(b, Base64.DEFAULT);
    }
}