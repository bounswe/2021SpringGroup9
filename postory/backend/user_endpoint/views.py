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

from .models import User
import jwt

from django.contrib.auth import get_user_model
User = get_user_model()

class AddPhoto(GenericAPIView):
    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404

    def post(self, request, format=None):
        userid = request.auth['user_id']
        data = dict(request.data)
        
        image = data['image'][0]
        imageObject = Image(file=image)
        imageObject.save()
        user = self.get_object(userid)
        user.userPhoto = imageObject
        try:
            user.save()
            return Response(status=status.HTTP_200_OK)
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
            return Response(status=status.HTTP_403_FORBIDDEN)
        if user2 in user1.followedUsers.all(): # if user1 already followed user2
            return Response(status=status.HTTP_409_CONFLICT)
        if user1.id==user2.id: # if the user wants to follow itself
            return Response(status=status.HTTP_400_BAD_REQUEST)
        
        try:
            user1.followedUsers.add(user2.id)
            user2.followerUsers.add(user1.id)

            user1.save()
            user2.save()
            return Response({'followed': user2.id, 'follower': user1.id}, status=status.HTTP_200_OK)
        except:
            return Response(status=status.HTTP_400_BAD_REQUEST)

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
        requester_user = User.objects.filter(id = user_id).first()
        requested_user = User.objects.filter(id = pk).first()
        requested_user.image = requested_user.image.file.url
        if(not requested_user.isPrivate or requested_user in requester_user.followedUsers):
            serializer = dict(UserSerializer(requested_user).data)
            return Response(serializer)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)