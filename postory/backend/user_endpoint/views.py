from django.http import Http404
from django.http import HttpResponse

from django.shortcuts import render
from rest_framework.generics import GenericAPIView
from rest_framework.response import Response

from rest_framework import status
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView
from rest_framework.decorators import api_view

import urllib.parse as urlparse
from urllib.parse import urlencode

from .models import User
from .serializers import UserSerializer, UserCreateSerializer
from rest_framework_simplejwt.tokens import RefreshToken, SlidingToken, UntypedToken
from django.contrib.auth.models import User

import requests
import json
import datetime
import environ
import jwt

from django.contrib.auth import get_user_model
User = get_user_model()

class UserFollowing(GenericAPIView):

    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except User.DoesNotExist:
            raise Http404

    def post(self, request, pk, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']

        user1 = self.get_object(pk=user_id)
        user2 = self.get_object(pk=pk)

        if user2.isPrivate: # if private can't follow
            return Response(status.HTTP_400_BAD_REQUEST)
        if user2 in user1.followedUsers.all(): # if user1 already followed user2
            return Response(status.HTTP_400_BAD_REQUEST)
        if user1.id==user2.id: # if the user wants to follow itself
            return Response(status.HTTP_400_BAD_REQUEST)
        
        user1.followedUsers.add(user2.id)
        user2.followerUsers.add(user1.id)

        user1.save()
        user2.save()
        return Response(status.HTTP_200_OK)
