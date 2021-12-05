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

import requests
import json
import datetime
import environ

class UserFollowing(GenericAPIView):

    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except User.DoesNotExist:
            raise Http404

    def post(self, request, pk1, pk2, format=None):
        user1 = self.get_object(pk=pk1)
        user2 = self.get_object(pk=pk2)

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

class GetUser(GenericAPIView):
    pass