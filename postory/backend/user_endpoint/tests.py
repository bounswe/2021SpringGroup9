from django.test import TestCase, Client
from user_endpoint.models import User
from rest_framework.test import APIClient, APITestCase
from rest_framework_simplejwt.tokens import RefreshToken
from django.urls import reverse
import pytest
import json 


class TestFollow(APITestCase):


    def setUp(self):
        self.client = Client()
        self.user1 = User.objects.create(
            id = 1, 
            username = "user1",
            name = "Name1",
            surname = "Surname1",
            email = "user1@email.com",
            isBanned = False,
            isAdmin = False,
            isPrivate = False,
            is_active = False,
        )
        self.user2 = User.objects.create(
            id = 2, 
            username = "user2",
            name = "Name2",
            surname = "Surname2",
            email = "user2@email.com",
            isBanned = False,
            isAdmin = False,
            isPrivate = False,
            is_active = False,
        )
        #self.user = User.objects.create_user('user1@email.com', password='Password123%', **{'username':"user1", 'name':'Name1', 'surname':'Surname1'})
        self.user1.followedUsers.set([2])
        self.user1.followerUsers.set([])
        self.user2.followedUsers.set([])
        self.user2.followerUsers.set([1])
        self.user = User.objects.create_user(email='user3@email.com', password='Password123%', username="user3", name='Name1', surname='Surname1')

        self.client = APIClient()
   

    def test_another_user_follow(self):
        # user_data = {
        #     "email":'user4@email.com', "password":'Password123%', "re_password":"Password123%","username":"user4", "name":'Name4', "surname":'Surname4'
        # }

        # res = self.client.post('/auth/users/', json.dumps(user_data), content_type='application/json')
        # print(res)        

        login_data = {
            "email": "user3@email.com",
            "password": "Password123%"
        }
        token = self.client.post('/auth/jwt/create/', json.dumps(login_data), content_type='application/json')
        self.client.credentials(Authorization=token.data['access'])

        response = self.client.post(reverse('follow_user', args=[self.user1.id]), format='json')
        assert response.status_code == 200

    def test_follow_self(self):
        login_data = {
            "email": "user3@email.com",
            "password": "Password123%"
        }
        token = self.client.post('/auth/jwt/create/', json.dumps(login_data), content_type='application/json')
        self.client.credentials(Authorization=token.data['access'])

        response = self.client.post(reverse('follow_user', args=[self.user.id]), format='json')
        assert response.status_code == 200

