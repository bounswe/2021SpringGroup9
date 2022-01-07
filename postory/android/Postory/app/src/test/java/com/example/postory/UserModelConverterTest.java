package com.example.postory;

import static org.junit.Assert.assertEquals;

import com.example.postory.models.OtherUser;
import com.example.postory.models.UserGeneralModel;
import com.example.postory.models.UserModel;
import com.example.postory.utils.UserModelConverter;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @author niyaziulke */

/*
    ID            : TC_A_4
    Description   : Tests whether public user model is correctly converted to the private.
 */
public class UserModelConverterTest {

    UserModel model1 = new UserModel();

    String model1Json = "{\"id\":13,\"username\":\"berkyilmaz\",\"password\":\"pbkdf2_sha256$260000$XQiS5fX6ufa0tX2FlcPiLn$nj37DnnnLDctlZyxaSQfcF4kIFjkwZdqdoMUJeB+58g=\",\"name\":\"Berk\",\"surname\":\"Yılmaz\",\"email\":\"melihozcnn@gmail.com\",\"followedUsers\":[{\"id\":2,\"username\":\"melihaydogd\",\"name\":\"Ahmet\",\"surname\":\"Aydogdu\",\"email\":\"amelih6@gmail.com\",\"isBanned\":false,\"isAdmin\":true,\"isPrivate\":true,\"is_active\":true,\"userPhoto\":\"\"},{\"id\":16,\"username\":\"mehmetboun\",\"name\":\"mehmet\",\"surname\":\"boun\",\"email\":\"tuldlatvpfltpveuwl@kvhrw.com\",\"isBanned\":false,\"isAdmin\":false,\"isPrivate\":false,\"is_active\":true,\"userPhoto\":\"\"},{\"id\":17,\"username\":\"leylaboun\",\"name\":\"leyla\",\"surname\":\"boun\",\"email\":\"leylaboun96@gmail.com\",\"isBanned\":true,\"isAdmin\":false,\"isPrivate\":false,\"is_active\":true,\"userPhoto\":\"\"}],\"followerUsers\":[{\"id\":2,\"username\":\"melihaydogd\",\"name\":\"Ahmet\",\"surname\":\"Aydogdu\",\"email\":\"amelih6@gmail.com\",\"isBanned\":false,\"isAdmin\":true,\"isPrivate\":true,\"is_active\":true,\"userPhoto\":\"\"},{\"id\":10,\"username\":\"mstfec\",\"name\":\"mstfec\",\"surname\":\"mstfec\",\"email\":\"mstfec@gmail.com\",\"isBanned\":false,\"isAdmin\":false,\"isPrivate\":true,\"is_active\":true,\"userPhoto\":\"\"}],\"posts\":[17,22],\"savedPosts\":[],\"likedPosts\":[],\"comments\":[],\"isBanned\":false,\"isAdmin\":false,\"isPrivate\":false,\"is_active\":true,\"userPhoto\":\"\"}";

    UserGeneralModel generalModel1 = new UserGeneralModel();

    @Before
    public void createModels() {
        Gson gson = new Gson();
        model1 = gson.fromJson(model1Json, UserModel.class);
    }

    @Test
    public void convertModelToGeneralModel(){
        generalModel1 = UserModelConverter.convert(model1);

        assertEquals(generalModel1.getId(), model1.getId());
        assertEquals(generalModel1.getEmail(),model1.getEmail());
        assertEquals(generalModel1.getName(),model1.getName());
        assertEquals(generalModel1.getSurname(), model1.getSurname());
        assertEquals(generalModel1.getUsername(), model1.getUsername());
        assertEquals(generalModel1.isAdmin(), model1.isAdmin());
        assertEquals(generalModel1.isBanned(),model1.isBanned());
        assertEquals(generalModel1.isIs_active(),model1.isIs_active());
        assertEquals(generalModel1.getUserPhoto(),model1.getUserPhoto());
        assertEquals(generalModel1.getComments(), model1.getComments());
        assertEquals(generalModel1.getPosts(), model1.getPosts());
        assertEquals(generalModel1.getLikedPosts(), model1.getLikedPosts());
        assertEquals(generalModel1.getSavedPosts(), model1.getSavedPosts());

        List<Integer> followedUsers_general = generalModel1.getFollowedUsers();
        List<OtherUser> followedUsers = model1.getFollowedUsers();

        assertEquals(followedUsers_general.size(),followedUsers.size());
        for(int i = 0; i < followedUsers_general.size(); i++){
            int id_general = followedUsers_general.get(i);
            int id = followedUsers.get(i).getId();
           assertEquals(id_general,id);
        }
        List<Integer> followerUsers_general = generalModel1.getFollowerUsers();
        List<OtherUser> followerUsers = model1.getFollowerUsers();

        assertEquals(followerUsers_general.size(),followerUsers.size());
        for(int i = 0; i < followerUsers_general.size(); i++){
            int id_general = followerUsers_general.get(i);
            int id = followerUsers.get(i).getId();
            assertEquals(id_general,id);
        }

    }


}


