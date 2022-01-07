from user_endpoint.models import User, FollowRequest, UserReport, StoryReport
from post_endpoint.models import Post
from rest_framework.test import APIClient, APITestCase
from django.urls import reverse


class TestFollow(APITestCase):
    def setUp(self):
        self.client = APIClient()
        user1_info = {
            "name": "Name1",
            "surname": "Surname1",
            "email" : "user1@email.com",
            "username": "user1",
            "password": "Password123%",
            "re_password": "Password123%"
        }
        user2_info = {
            "name": "Name2",
            "surname": "Surname2",
            "email" : "user2@email.com",
            "username": "user2",
            "password": "Password123%",
            "re_password": "Password123%"
        }
        user3_info = {
            "name": "Name3",
            "surname": "Surname3",
            "email" : "user3@email.com",
            "username": "user3",
            "password": "Password123%",
            "re_password": "Password123%"
        }
        user4_info = {
            "name": "Name4",
            "surname": "Surname4",
            "email" : "user4@email.com",
            "username": "user4",
            "password": "Password123%",
            "re_password": "Password123%"
        }
        self.client.post("/auth/users/",user1_info)
        self.client.post("/auth/users/",user2_info)
        self.client.post("/auth/users/",user3_info)
        self.client.post("/auth/users/",user4_info)
        self.user1 = User.objects.filter(username = "user1").first()
        self.user2 = User.objects.filter(username = "user2").first()
        self.user3 = User.objects.filter(username = "user3").first()
        self.user4 = User.objects.filter(username = "user4").first()
        self.user1.is_active = True
        self.user2.is_active = True
        self.user3.is_active = True
        self.user4.is_active = True
        self.user1.isPrivate = True
        self.user4.isPrivate = True
        self.user1.followedUsers.set([])
        self.user1.followerUsers.set([])
        self.user2.followedUsers.set([self.user3])
        self.user2.followerUsers.set([])
        self.user3.followedUsers.set([])
        self.user3.followerUsers.set([self.user2])
        self.user4.followedUsers.set([])
        self.user4.followerUsers.set([])
        self.user1.save()
        self.user2.save()
        self.user3.save()
        self.user4.save()

        self.request1 = FollowRequest.objects.create(
            fromUser = self.user2,
            toUser = self.user4
        )
        self.request2 = FollowRequest.objects.create(
            fromUser = self.user4,
            toUser = self.user1
        )

        self.post1 = Post.objects.create(
            title = 'Title1',
            story = 'Story1',
            owner = self.user1.id,
            username = self.user1.username
        )

    def test_another_user_follow(self):
        """
        ID            : TC_B_U_1
        Title         : User Follow - Following public user test
        Test Priority : High
        Module Name   : Backend - User Follow
        Description   : Checks whether a user can follow another user with a public profile or not. 
        Status (Fail/Pass):  Fails if follow fails. Passes if the user is in the followed list and the other user is in the follower list. 
        """
        token = self.client.post("/auth/jwt/create",{"email":"user1@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('follow_user', args=[self.user2.id]), format='json')

        assert response.status_code == 200
        assert self.user2 in self.user1.followedUsers.all()
        assert self.user1 in self.user2.followerUsers.all()


    def test_follow_self(self):
        """
        ID            : TC_B_U_2
        Title         : User Follow - Following yourself
        Test Priority : High
        Module Name   : Backend - User Follow
        Description   : Checks whether a user can follow herself/himself or not. 
        Status (Fail/Pass):  Fails if he/she can follow himself/herself. Passes if the user is not in his/her followed or follower list. 
        """
        token = self.client.post("/auth/jwt/create",{"email":"user2@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('follow_user', args=[self.user2.id]), format='json')

        assert response.status_code == 400
        assert self.user2 not in self.user2.followedUsers.all()
        assert self.user2 not in self.user2.followerUsers.all()

    def test_unfollow(self):
        """
        ID            : TC_B_U_3
        Title         : User Follow - Unfollowing a followed user
        Test Priority : High
        Module Name   : Backend - User Follow
        Description   : Checks whether a user can unfollow a user he/she followed before. 
        Status (Fail/Pass):  Fails if unfollow fails. Passes if the requested user is removed from followed and the other user is removed from followers. 
        """
        token = self.client.post("/auth/jwt/create",{"email":"user2@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('follow_user', args=[self.user3.id]), format='json')

        assert response.status_code == 200
        assert self.user3 not in self.user2.followedUsers.all()
        assert self.user2 not in self.user3.followerUsers.all()

    def test_follow_private(self):
        """
        ID            : TC_B_U_4
        Title         : User Follow - Following private account
        Test Priority : High
        Module Name   : Backend - User Follow
        Description   : Checks whether a user can send follow request to a user with private account or not. 
        Status (Fail/Pass): Fails if sending request fails. Passes if request object is created. 
        """
        token = self.client.post("/auth/jwt/create",{"email":"user2@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('follow_user', args=[self.user1.id]), format='json')

        assert response.status_code == 200
        assert FollowRequest.objects.filter(fromUser=self.user2, toUser=self.user1).exists()

    def test_accept_request(self):
        """
        ID            : TC_B_U_5
        Title         : User Follow - Accept request
        Test Priority : High
        Module Name   : Backend - User Follow
        Description   : Checks whether a user can accept a pending follow request or not. 
        Status (Fail/Pass): Fails if accept request fails. Passes if successfuly accepted. 
        """
        token = self.client.post("/auth/jwt/create",{"email":"user4@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('accept_pending_request', args=[self.user2.id]), format='json')

        assert response.status_code == 200
        assert not FollowRequest.objects.filter(fromUser=self.user2, toUser=self.user4).exists()
        assert self.user4 in self.user2.followedUsers.all()
        assert self.user2 in self.user4.followerUsers.all()

    def test_decline_request(self):
        """
        ID            : TC_B_U_6
        Title         : User Follow - Decline request
        Test Priority : High
        Module Name   : Backend - User Follow
        Description   : Checks whether a user can decline a pending follow request or not. 
        Status (Fail/Pass): Fails if decline request fails. Passes if successfuly declined. 
        """
        token = self.client.post("/auth/jwt/create",{"email":"user1@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('decline_pending_request', args=[self.user4.id]), format='json')

        assert response.status_code == 200
        assert not FollowRequest.objects.filter(fromUser=self.user4, toUser=self.user1).exists()
        assert self.user1 not in self.user4.followedUsers.all()
        assert self.user4 not in self.user1.followerUsers.all()

    def test_report_user(self):
        """
        ID            : TC_B_U_7
        Title         : Report - User Report
        Test Priority : High
        Module Name   : Backend - User Report
        Description   : Checks whether a user can report another user or not. 
        Status (Fail/Pass): Fails if there is no corresponding UserReport object. Passes if object created. 
        """
        token = self.client.post("/auth/jwt/create",{"email":"user1@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('user_report', args=[self.user4.id]), format='json')

        assert response.status_code == 200
        assert UserReport.objects.filter(fromUser=self.user1, toUser=self.user4).exists()

    def test_report_story(self):
        """
        ID            : TC_B_U_8
        Title         : Report - Story Report
        Test Priority : High
        Module Name   : Backend - Story Report
        Description   : Checks whether a user can report another story or not. 
        Status (Fail/Pass): Fails if there is no corresponding StoryReport object. Passes if object created. 
        """
        token = self.client.post("/auth/jwt/create",{"email":"user4@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('story_report', args=[self.post1.id]), format='json')

        assert response.status_code == 200
        assert StoryReport.objects.filter(fromStory=self.user4, toStory=self.post1).exists()

    def test_change_private(self):
        """
        ID            : TC_B_U_9
        Title         : User Profile - Change Account Settings
        Test Priority : Medium
        Module Name   : Backend - User Profile Change
        Description   : Checks whether a user can change his/her profile's privacy setting. 
        Status (Fail/Pass): Passes if status code is 200. Fails otherwise.
        """
        token = self.client.post("/auth/jwt/create",{"email":"user3@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.put(reverse('change_profile_settings'), format='json')
        
        assert response.status_code == 200