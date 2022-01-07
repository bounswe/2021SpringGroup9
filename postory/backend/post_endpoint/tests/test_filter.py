from user_endpoint.models import User, FollowRequest, UserReport, StoryReport
from post_endpoint.models import Post
from rest_framework.test import APIClient, APITestCase
from django.urls import reverse


class TestFollow(APITestCase):
    def setUp(self):
        self.client = APIClient()
        
        """
        Create users
        """
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
        self.user1.followedUsers.set([])
        self.user1.followerUsers.set([])
        self.user2.followedUsers.set([self.user3])
        self.user2.followerUsers.set([])
        self.user3.followedUsers.set([])
        self.user3.followerUsers.set([self.user2])
        self.user4.followedUsers.set([])
        self.user4.followerUsers.set([])
        
        """
        Save users
        """
        self.user1.save()
        self.user2.save()
        self.user3.save()
        self.user4.save()

        """
        Create posts
        """
        self.post1_1 = Post.objects.create(
            id = 1,
            title = "Title1_1",
            story = "Story1_1",
            owner = self.user1.id
        )
        self.post1_2 = Post.objects.create(
            id = 2,
            title = "Title1_2",
            story = "Story1_2",
            owner = self.user1.id
        )
        self.post2_1 = Post.objects.create(
            id = 3,
            title = "Title2_1",
            story = "Story2_1",
            owner = self.user2.id
        )
        self.post2_2 = Post.objects.create(
            id = 4,
            title = "Title2_2",
            story = "Story2_2",
            owner = self.user2.id
        )
        self.post3_1 = Post.objects.create(
            id = 5,
            title = "Title3_1",
            story = "Story3_1",
            owner = self.user3.id
        )
        self.post3_2 = Post.objects.create(
            id = 6,
            title = "Title3_2",
            story = "Story3_2",
            owner = self.user3.id
        )
        self.post4_1 = Post.objects.create(
            id = 7,
            title = "Title4_1",
            story = "Story4_1",
            owner = self.user4.id
        )
        self.post4_2 = Post.objects.create(
            id = 8,
            title = "Title4_2",
            story = "Story4_2",
            owner = self.user4.id
        )

    """
    ID            : TC_B_P_4
    Title         : Posts Endpoint - Posts Filter
    Test Priority : High
    Module Name   : Frontend - Get Filtered Posts
    Description   : Checks whether the filtered posts return successfully.
    """
    def test_search_and_filter(self):
        token = self.client.post("/auth/jwt/create",{"email":"user1@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        url = "/api/post/all/filter?keyword=Title4_1&user=user4"
        response = self.client.get(url, format='json')

        assert response.status_code == 200
        assert response.json()[0]['title'] == 'Title4_1'