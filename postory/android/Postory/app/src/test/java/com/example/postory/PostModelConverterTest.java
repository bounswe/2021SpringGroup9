package com.example.postory;

import static org.junit.Assert.assertEquals;

import com.example.postory.models.OtherUser;
import com.example.postory.models.Post;
import com.example.postory.models.UserGeneralModel;
import com.example.postory.models.UserModel;
import com.example.postory.utils.UserModelConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */


/**
 *
 * This is a Test class which checks whether an example JSON response returned from our backend
 * is parsed correctly by comparing each field with a model that we know has the true values for it.
 *
 * @author melihozcan
 *
 *
 */

public class PostModelConverterTest {

    Post modelGround = new Post();
    Post modelParsed = new Post();

    String model1Json = "{\"id\":9,\"title\":\"Goat in Ölüdeniz\",\"story\":\"Seeing a goat near the beach eating some bread pieces was really a unique experience for me.\",\"owner\":1,\"username\":\"ahmetkus\",\"tags\":[\"fun\",\"vacation\",\"summer\"],\"locations\":[[\"Ölüdeniz\",36.67366496090383,29.102678261697296]],\"year\":[2020,2020],\"month\":[6,7],\"day\":[],\"hour\":[],\"minute\":[],\"images\":[\"https://group9-451-project.s3.amazonaws.com/image/IMG_20210710_201947.jpg\"],\"postDate\":\"2022-01-04T09:45:27.687000Z\",\"editDate\":\"2022-01-04T09:45:27.687000Z\",\"viewCount\":0,\"comments\":[],\"likeList\":[[6.0,\"mstfec\"]],\"userPhoto\":\"\"}";



    @Before
    public void createModels() throws ParseException {

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").create();
        modelParsed = gson.fromJson(model1Json, Post.class);
        modelGround.setId(9);
        modelGround.setTitle("Goat in Ölüdeniz");
        modelGround.setStory("Seeing a goat near the beach eating some bread pieces was really a unique experience for me.");
        modelGround.setOwner("1");
        modelGround.setUsername("ahmetkus");
        ArrayList<String> tags = new ArrayList<>();
        tags.add("fun");

        tags.add("vacation");
        tags.add("summer");
        modelGround.setTags(tags);
        modelGround.setComments(new ArrayList<>());
        modelGround.setUserPhoto(null);
        List<Object> locations = new ArrayList<>();
        List<List<Object>> locationsList = new ArrayList<>();
        List<Object> likeList = new ArrayList<>();
        List<List<Object>> likes = new ArrayList<>();
        likeList.add(6.0);
        modelGround.setViewCount(0);
        likeList.add("mstfec");
        locations.add("Ölüdeniz");
        likes.add(likeList);
        locations.add(36.67366496090383);
        locations.add(29.102678261697296);
        locationsList.add(locations);
        ArrayList<Integer> years = new ArrayList<>();
        ArrayList<Integer> months = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();
        images.add("https://group9-451-project.s3.amazonaws.com/image/IMG_20210710_201947.jpg");
        years.add(2020);
        years.add(2020);
        months.add(6);
        months.add(7);
        modelGround.setLocations(locationsList);
        modelGround.setYear(years);
        modelGround.setMonth(months);
        modelGround.setMinute(new ArrayList<>());
        modelGround.setHour(new ArrayList<>());
        modelGround.setDay(new ArrayList<>());
        modelGround.setImages(images);
        modelGround.setLikeList(likes);
        modelGround.setEditDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse("2022-01-04T09:45:27.687000Z"));
        modelGround.setPostDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse("2022-01-04T09:45:27.687000Z"));

    }


    @Test
    public void convertModelToGeneralModel(){
        assertEquals(modelGround.getId(), modelParsed.getId());
        assertEquals(modelGround.getStory(), modelParsed.getStory());
        assertEquals(modelGround.getComments(), modelParsed.getComments());
        assertEquals(modelGround.getLocations(), modelParsed.getLocations());
        assertEquals(modelGround.getLikeList(), modelParsed.getLikeList());
        assertEquals(modelGround.getDay(), modelParsed.getDay());
        assertEquals(modelGround.getHour(), modelParsed.getHour());
        assertEquals(modelGround.getMinute(), modelParsed.getMinute());
        assertEquals(modelGround.getYear(), modelParsed.getYear());
        assertEquals(modelGround.getMonth(), modelParsed.getMonth());
        assertEquals(modelGround.getTitle(), modelParsed.getTitle());
        assertEquals(modelGround.getOwner(), modelParsed.getOwner());
        assertEquals(modelGround.getUsername(), modelParsed.getUsername());
        assertEquals(modelGround.getTags(), modelParsed.getTags());
        assertEquals(modelGround.getImages(), modelParsed.getImages());
        assertEquals(modelGround.getPostDate(), modelParsed.getPostDate());
        assertEquals(modelGround.getEditDate(), modelParsed.getEditDate());
    }


}


