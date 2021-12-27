from user_endpoint.models import User
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
        self.client.post("/auth/users/",user1_info)
        self.client.post("/auth/users/",user2_info)
        self.client.post("/auth/users/",user3_info)
        self.user1 = User.objects.filter(username = "user1").first()
        self.user2 = User.objects.filter(username = "user2").first()
        self.user3 = User.objects.filter(username = "user3").first()
        self.user1.is_active = True
        self.user2.is_active = True
        self.user3.is_active = True
        self.user1.isPrivate = True
        self.user1.followedUsers.set([])
        self.user1.followerUsers.set([])
        self.user2.followedUsers.set([self.user3.id])
        self.user2.followerUsers.set([])
        self.user3.followedUsers.set([])
        self.user3.followerUsers.set([self.user2.id])
        self.user1.save()
        self.user2.save()
        self.user3.save()

    def test_another_user_follow(self):
        token = self.client.post("/auth/jwt/create",{"email":"user1@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('follow_user', args=[self.user2.id]), format='json')

        assert response.status_code == 200


    def test_follow_self(self):
        token = self.client.post("/auth/jwt/create",{"email":"user2@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('follow_user', args=[self.user2.id]), format='json')

        assert response.status_code == 400

    def test_follow_again(self):
        token = self.client.post("/auth/jwt/create",{"email":"user2@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('follow_user', args=[self.user3.id]), format='json')

        assert response.status_code == 409

    def test_follow_private(self):
        token = self.client.post("/auth/jwt/create",{"email":"user2@email.com","password":"Password123%"})
        assert token.status_code==200
        
        access_token = token.json()['access']
        self.client.credentials(HTTP_AUTHORIZATION=f"JWT {access_token}")

        response = self.client.post(reverse('follow_user', args=[self.user1.id]), format='json')

        assert response.status_code == 403