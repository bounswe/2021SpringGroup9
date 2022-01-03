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
        token = self.client.post("/auth/jwt/create",{"email":"user1@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('follow_user', args=[self.user2.id]), format='json')

        assert response.status_code == 200
        assert self.user2 in self.user1.followedUsers.all()
        assert self.user1 in self.user2.followerUsers.all()


    def test_follow_self(self):
        token = self.client.post("/auth/jwt/create",{"email":"user2@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('follow_user', args=[self.user2.id]), format='json')

        assert response.status_code == 400
        assert self.user2 not in self.user2.followedUsers.all()
        assert self.user2 not in self.user2.followerUsers.all()

    def test_unfollow(self):
        token = self.client.post("/auth/jwt/create",{"email":"user2@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('follow_user', args=[self.user3.id]), format='json')

        assert response.status_code == 200
        assert self.user3 not in self.user2.followedUsers.all()
        assert self.user2 not in self.user3.followerUsers.all()

    def test_follow_private(self):
        token = self.client.post("/auth/jwt/create",{"email":"user2@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('follow_user', args=[self.user1.id]), format='json')

        assert response.status_code == 200
        assert FollowRequest.objects.filter(fromUser=self.user2, toUser=self.user1).exists()

    def test_accept_request(self):
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
        token = self.client.post("/auth/jwt/create",{"email":"user1@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('user_report', args=[self.user4.id]), format='json')

        assert response.status_code == 200
        assert UserReport.objects.filter(fromUser=self.user1, toUser=self.user4).exists()

    def test_report_story(self):
        token = self.client.post("/auth/jwt/create",{"email":"user4@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('story_report', args=[self.post1.id]), format='json')

        assert response.status_code == 200
        assert StoryReport.objects.filter(fromStory=self.user4, toStory=self.post1).exists()

    def test_change_private(self):
        token = self.client.post("/auth/jwt/create",{"email":"user3@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.put(reverse('change_profile_settings'), format='json')
        
        assert response.status_code == 200