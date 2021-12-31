from django.test import TestCase
from .models import Post
from user_endpoint.models import User
from rest_framework.authtoken.models import Token
from rest_framework.test import APIClient, APITestCase

# Create your tests here.

def setUpUsers():
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
    user4 = {
        "name": "Name4",
        "surname": "Surname4",
        "email" : "user4@mail.com",
        "username": "user4",
        "password": "PASSword123*",
        "re_password": "PASSword123*"
    }
    apiClient.post("/auth/users/",user1)
    apiClient.post("/auth/users/",user2)
    apiClient.post("/auth/users/",user3)
    apiClient.post("/auth/users/",user4)
    user1Object = User.objects.filter(username = "user1").first()
    user2Object = User.objects.filter(username = "user2").first()
    user3Object = User.objects.filter(username = "user3").first()
    user4Object = User.objects.filter(username = "user4").first()
    user1Object.is_active = True
    user2Object.is_active = True
    user3Object.is_active = True
    user4Object.is_active = True
    user4Object.isPrivate = True
    user1Object.save()
    user2Object.save()
    user3Object.save()
    user4Object.save()

def setUpPosts(count):
    owner1 = count*4 + 1
    owner2 = count*4 + 2
    owner3 = count*4 + 3
    owner4 = count*4 + 4
    post1_1 = Post.objects.create(
        id = 1,
        title = "Title1_1",
        story = "Story1_1",
        owner = owner1
    )
    post1_2 = Post.objects.create(
        id = 2,
        title = "Title1_2",
        story = "Story1_2",
        owner = owner1
    )
    post2_1 = Post.objects.create(
        id = 3,
        title = "Title2_1",
        story = "Story2_1",
        owner = owner2
    )
    post2_2 = Post.objects.create(
        id = 4,
        title = "Title2_2",
        story = "Story2_2",
        owner = owner2
    )
    post3_1 = Post.objects.create(
        id = 5,
        title = "Title3_1",
        story = "Story3_1",
        owner = owner3
    )
    post3_2 = Post.objects.create(
        id = 6,
        title = "Title3_2",
        story = "Story3_2",
        owner = owner3
    )
    post4_1 = Post.objects.create(
        id = 7,
        title = "Title4_1",
        story = "Story4_1",
        owner = owner4
    )
    post4_2 = Post.objects.create(
        id = 8,
        title = "Title4_2",
        story = "Story4_2",
        owner = owner4
    )
class GetPosts(APITestCase):
    def setUp(self):
        setUpUsers()
    
    def test_DiscoverPosts(self):
        setUpPosts(0)
        apiClient = APIClient()
        resp = apiClient.post("/auth/jwt/create",{"email":"user1@mail.com","password":"PASSword123*"})
        token = resp.json()["access"]
        apiClient.credentials(HTTP_AUTHORIZATION="JWT "+token)
        resp = apiClient.get("/api/post/all/discover")
        posts = resp.json()
        ids = [post['id'] for post in posts]
        self.assertCountEqual(ids,[1,2,3,4,5,6])

    def test_FollowedUserPosts(self):
        setUpPosts(1)
        apiClient = APIClient()
        resp = apiClient.post("/auth/jwt/create",{"email":"user1@mail.com","password":"PASSword123*"})
        token = resp.json()["access"]
        apiClient.credentials(HTTP_AUTHORIZATION="JWT "+token)
        resp = apiClient.post("/api/user/follow/6")
        resp = apiClient.get("/api/post/all")
        posts = resp.json()
        ids = [post['id'] for post in posts]
        self.assertCountEqual(ids,[1,2,3,4])

    def test_SpecificUserPosts(self):
        setUpPosts(2)
        apiClient = APIClient()
        resp = apiClient.post("/auth/jwt/create",{"email":"user1@mail.com","password":"PASSword123*"})
        token = resp.json()["access"]
        apiClient.credentials(HTTP_AUTHORIZATION="JWT "+token)
        resp = apiClient.get("/api/post/all/user/10")
        postsOfUser10 = resp.json()
        ids = [post['id'] for post in postsOfUser10]
        self.assertCountEqual(ids,[3,4]) 


