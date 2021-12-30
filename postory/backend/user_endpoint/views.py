from django.http import Http404
from django.http import HttpResponse

from django.shortcuts import render
from rest_framework.generics import GenericAPIView
from rest_framework.response import Response

from rest_framework import status
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView

from .serializers import UserSerializer, FollowRequestSerializer
from post_endpoint.models import Post, Image
from post_endpoint.serializers import PostSerializer

from .models import User, FollowRequest
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
        user.userPhoto.set([imageObject])
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
        data = dict([])

        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']

        user1 = self.get_object(pk=user_id)
        user2 = self.get_object(pk=pk)

        if user1.id==user2.id: # if the user wants to follow itself
            return Response({"message": "can't follow yourself"}, status=status.HTTP_400_BAD_REQUEST)
        
        elif user2 in user1.followedUsers.all(): # if user1 already followed user2, unfollow
            try:
                user1.followedUsers.remove(user2)
                user2.followerUsers.remove(user1)

                user1.save()
                user2.save()
                return Response({'message': f"{user1.id} successfuly unfollowed {user2.id}"}, status=status.HTTP_200_OK)
            except:
                return Response({"message": "unfollow failed"}, status=status.HTTP_400_BAD_REQUEST)

        elif user2.isPrivate: # if private can't follow
            try:
                data['fromUser'] = user1.id
                data['toUser'] = user2.id

                if FollowRequest.objects.filter(fromUser=user1, toUser=user2).exists():
                    return Response({"message": "request already sent"}, status=status.HTTP_400_BAD_REQUEST)
                
                serializer = FollowRequestSerializer(data=data)
                if serializer.is_valid():
                    serializer.save()
                    return Response({"message": "succesfuly sent request to private profile", 'data': serializer.data}, status=status.HTTP_200_OK)
                else:
                    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
            except:
                return Response({"message": "private profile follow failed"}, status=status.HTTP_400_BAD_REQUEST)
        
        else: #follow
            try:
                user1.followedUsers.add(user2)
                user2.followerUsers.add(user1)

                user1.save()
                user2.save()
                return Response({'message': f"{user1.id} successfuly followed {user2.id}"}, status=status.HTTP_200_OK)
            except:
                return Response({"message": "follow failed"}, status=status.HTTP_400_BAD_REQUEST)

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
        if(not requested_user.isPrivate or requested_user in requester_user.followedUsers.all()):
            serializer = dict(UserSerializer(requested_user).data)
            serializer = get_user(requested_user, serializer)
            return Response(serializer)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)

def get_user(user, serializer):
    followers = []
    for followerUser in user.followerUsers.all():
        temp = {}
        temp['id'] = followerUser.id
        temp['username'] = followerUser.username
        temp['name'] = followerUser.name
        temp['surname'] = followerUser.surname
        temp['email'] = followerUser.email
        temp['isBanned'] = followerUser.isBanned
        temp['isAdmin'] = followerUser.isAdmin
        temp['isPrivate'] = followerUser.isPrivate
        temp['is_active'] = followerUser.is_active
        followers.append(temp)
    followed = []
    for followedUser in user.followedUsers.all():
        temp = {}
        temp['id'] = followedUser.id
        temp['username'] = followedUser.username
        temp['name'] = followedUser.name
        temp['surname'] = followedUser.surname
        temp['email'] = followedUser.email
        temp['isBanned'] = followedUser.isBanned
        temp['isAdmin'] = followedUser.isAdmin
        temp['isPrivate'] = followedUser.isPrivate
        temp['is_active'] = followedUser.is_active
        followed.append(temp)
    serializer['followerUsers'] = followers
    serializer['followedUsers'] = followed
    try:
        userPhoto = Image.objects.filter(user = user.id).first()
        serializer['userPhoto'] = userPhoto.file.url
    except:
        serializer['userPhoto'] = ""
    return serializer