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
import activityStream.views as activityStream

from .models import User
import jwt
from django.urls.base import resolve
from django.urls import reverse

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
        user.userPhoto.set([imageObject])
        try:
            user.save()
            activityStream.createActivity(userid,"added photo",imageObject.id,resolve(request.path_info).route,"UserAddPhoto",True)
            return Response(status=status.HTTP_200_OK)
        except:
            activityStream.createActivity(userid,"added photo","None",resolve(request.path_info).route,"UserAddPhoto",False)
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
            activityStream.createActivity(user1.id,"followed",user2.id,resolve(request.path_info).route,"UserFollow",False)       
            return Response(status=status.HTTP_403_FORBIDDEN)
        if user2 in user1.followedUsers.all(): # if user1 already followed user2
            activityStream.createActivity(user1.id,"followed",user2.id,resolve(request.path_info).route,"UserFollow",False)
            return Response(status=status.HTTP_409_CONFLICT)
        if user1.id==user2.id: # if the user wants to follow itself
            activityStream.createActivity(user1.id,"followed",user2.id,resolve(request.path_info).route,"UserFollow",False)
            return Response(status=status.HTTP_400_BAD_REQUEST)
        
        try:
            user1.followedUsers.add(user2.id)
            user2.followerUsers.add(user1.id)

            user1.save()
            user2.save()
            activityStream.createActivity(user1.id,"followed",user2.id,resolve(request.path_info).route,"UserFollow",True)
            return Response({'followed': user2.id, 'follower': user1.id}, status=status.HTTP_200_OK)
        except:
            activityStream.createActivity(user1.id,"followed",user2.id,resolve(request.path_info).route,"UserFollow",False)
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
        if(not requested_user.isPrivate or requested_user in requester_user.followedUsers):
            serializer = dict(UserSerializer(requested_user).data)
            try:
                userPhoto = Image.objects.filter(user = requested_user.id).first()
                serializer['userPhoto'] = userPhoto.file.url
            except:
                serializer['userPhoto'] = ""
            activityStream.createActivity(requester_user.id,"requested user",requested_user.id,resolve(request.path_info).route,"UserRequest",True)
            return Response(serializer)
        else:
            activityStream.createActivity(requester_user.id,"requested user",requested_user.id,resolve(request.path_info).route,"UserRequest",False)
            return Response(status=status.HTTP_401_UNAUTHORIZED)