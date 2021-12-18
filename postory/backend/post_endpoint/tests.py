from django.test import TestCase
from .models import Post
from user_endpoint.models import User
from rest_framework.authtoken.models import Token
from rest_framework.test import APIClient, APITestCase

# Create your tests here.

class GetPosts(APITestCase):
    token = ""
    def setUp(self):
        apiClient = APIClient()
        user1 = User.objects.create(
            id = 1, 
            username = "user1",
            name = "Name1",
            password = "password",
            surname = "Surname1",
            email = "user1@email.com",
            isBanned = False,
            isAdmin = False,
            isPrivate = False,
            is_active = True,
        )
        user2 = User.objects.create(
            id = 2, 
            username = "user2",
            name = "Name2",
            password = "password",
            surname = "Surname2",
            email = "user2@email.com",
            isBanned = False,
            isAdmin = False,
            isPrivate = False,
            is_active = True,
        )
        user3 = User.objects.create(
            id = 3, 
            username = "user3",
            name = "Name3",
            password = "password",
            surname = "Surname3",
            email = "user3@email.com",
            isBanned = False,
            isAdmin = False,
            isPrivate = False,
            is_active = True,
        )
        post2_1 = Post.objects.create(
            id = 1,
            title = "Title2_1",
            story = "Story2_1",
            owner = 2
        )
        post3_1 = Post.objects.create(
            id = 2,
            title = "Title3_1",
            story = "Story3_1",
            owner = 3
        )

    def test_AdminGetPosts(self):
        apiClient = APIClient()
        apiClient.credentials(HTTP_AUTHORIZATION="JWT deneme")
        resp = apiClient.get("/api/post/all/admin")
        print(Post.objects.filter(id = 1).first().title)
        assert resp.status_code==401

    def test_OnlyFollowedPosts(self):
        apiClient = APIClient()
        token = apiClient.post("/auth/jwt/create",{"email":"user1@email.com","password":"password"})
        assert token.status_code==401
        
