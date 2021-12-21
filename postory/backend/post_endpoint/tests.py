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
        user1 = {
            "name": "Name1",
            "surname": "Surname1",
            "email" : "user1@mail.com",
            "username": "user1",
            "password": "PASSword123*",
            "re_password": "PASSword123*"
        }
        user2 = {
            "name": "Name2",
            "surname": "Surname2",
            "email" : "user2@mail.com",
            "username": "user2",
            "password": "PASSword123*",
            "re_password": "PASSword123*"
        }
        user3 = {
            "name": "Name3",
            "surname": "Surname3",
            "email" : "user3@mail.com",
            "username": "user3",
            "password": "PASSword123*",
            "re_password": "PASSword123*"
        }
        apiClient.post("/auth/users/",user1)
        apiClient.post("/auth/users/",user2)
        apiClient.post("/auth/users/",user3)
        user1Object = User.objects.filter(username = "user1").first()
        user2Object = User.objects.filter(username = "user2").first()
        user3Object = User.objects.filter(username = "user3").first()
        user1Object.is_active = True
        user2Object.is_active = True
        user3Object.is_active = True
        user1Object.save()
        user2Object.save()
        user3Object.save()
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
            owner = 2
        )

    def test_AdminGetPosts(self):
        apiClient = APIClient()
        apiClient.credentials(HTTP_AUTHORIZATION="JWT deneme")
        resp = apiClient.get("/api/post/all/admin")
        assert resp.status_code==401

    def test_OnlyFollowedPosts(self):
        apiClient = APIClient()
        print(User.objects.filter(username = "user1").first().is_active)
        token = apiClient.post("/auth/jwt/create",{"email":"user1@mail.com","password":"PASSword123*"})
        print(token.json())
        assert token.status_code==200
        
