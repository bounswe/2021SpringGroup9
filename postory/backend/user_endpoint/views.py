from django.http import Http404
from django.http import HttpResponse

from django.shortcuts import render
from rest_framework.generics import GenericAPIView
from rest_framework.response import Response

from rest_framework import status
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView

from .models import User
from .serializers import UserSerializer
from post_endpoint.models import Post, Image
from post_endpoint.serializers import PostSerializer

class AddPhoto(GenericAPIView):
    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404

    def post(self, request, format=None):
        userid = request.auth['user_id']
        data = dict(request.data)
        
        images = data['images']
        imagesList = []
        for image in images:
            if image:
                imageObject = Image(file=image)
                imageObject.save()
                imagesList.append(imageObject.id)
        
        user = self.get_object(userid)
        user.images.set(imagesList)
    
        try:
            user.save()
        except:
            return Response(status=status.HTTP_400_BAD_REQUEST)
        
class SearchUser(GenericAPIView):

    def post(self, request, term, format=None):
        users = User.objects.filter(username__contains=term)
        values = []
        for user in users:
            data = dict(UserSerializer(user).data)
            values.append(data)
        return Response(values, status=200)
        
def get_user(user):
    # Blog.objects.filter(pk__in=[1, 4, 7])
    pass
    
from rest_framework.decorators import api_view

import urllib.parse as urlparse
from urllib.parse import urlencode

from .models import User
from .serializers import UserSerializer, UserCreateSerializer
from rest_framework_simplejwt.tokens import RefreshToken, SlidingToken, UntypedToken
from django.contrib.auth.models import User
from django.core import serializers

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
        
        try:
            user1.followedUsers.add(user2.id)
            user2.followerUsers.add(user1.id)

            user1.save()
            user2.save()
            return Response(status.HTTP_200_OK)
        except:
            return Response(status.HTTP_400_BAD_REQUEST)

class UserGet(GenericAPIView):

    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except User.DoesNotExist:
            raise Http404
    
    def get(self, request, pk, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        user = User.objects.filter(id = user_id).first()

        if(user.isAdmin):
            serializer = dict(UserSerializer(user).data)
            return Response(serializer)
        else:
            return Response(status.HTTP_401_UNAUTHORIZED)