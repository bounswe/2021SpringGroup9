from django.http import Http404
from django.http import HttpResponse

from django.shortcuts import render
from rest_framework.generics import GenericAPIView
from rest_framework.response import Response
from rest_framework.decorators import api_view

from rest_framework import status
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView

from .serializers import UserSerializer, FollowRequestSerializer, UserReportSerializer, StoryReportSerializer
from post_endpoint.models import Post, Image
from post_endpoint.serializers import PostSerializer

import activityStream.views as activityStream

from django.core.mail import send_mail

from django.utils import timezone
from .models import User, FollowRequest, StoryReport, UserReport
import jwt
from django.urls.base import resolve
from django.urls import reverse

from django.contrib.auth import get_user_model
User = get_user_model()

class AddPhoto(GenericAPIView):
    """
    Creates an image object given the input image file. 
    Adds the image to the user object whose id is taken from jwt token. 
    """
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

class ChangePrivate(GenericAPIView):
    """
    Change profile settings private to public or public to private
    """
    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except User.DoesNotExist:
            raise Http404

    def put(self, request, format = None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        user = self.get_object(pk=user_id)

        try:
            user.isPrivate = not user.isPrivate
            user.save()

            activityStream.createActivity(user.id,"changed profile settings",user.id,resolve(request.path_info).route,"UserChangePrivate",True) 
            return Response(status=status.HTTP_200_OK)
        except:
            activityStream.createActivity(user.id,"changed profile settings",user.id,resolve(request.path_info).route,"UserChangePrivate",False) 
            return Response(status=status.HTTP_400_BAD_REQUEST)
        
class SearchUser(GenericAPIView):
    """
    Searches users by using the term parameter.
    Returns users that containes the term parameter.
    """
    def post(self, request, term, format=None):
        userid = request.auth['user_id']
        users = User.objects.filter(username__contains=term)
        values = []
        for user in users:
            data = get_user(user)
            values.append(data)
        activityStream.createActivity(userid,"searched users","search",resolve(request.path_info).route,"SearchUser",True)
        return Response(values, status=200)
        
    
class UserFollowing(GenericAPIView):
    """
    Follows other users if they have public account. If they have private accounts, sends follow request. 
    A user cannot follow herselfs/himself.
    Returns a message and an HTTP status code
    """
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
            print("AAA")
            activityStream.createActivity(user1.id,"followed",user2.id,resolve(request.path_info).route,"UserFollow",False) 
            return Response({"message": "can't follow yourself"}, status=status.HTTP_400_BAD_REQUEST)
        
        elif user2 in user1.followedUsers.all(): # if user1 already followed user2, unfollow
            try:
                user1.followedUsers.remove(user2)
                user2.followerUsers.remove(user1)

                user1.save()
                user2.save()
                activityStream.createActivity(user1.id,"unfollowed",user2.id,resolve(request.path_info).route,"UserUnfollow",True) 
                return Response({'message': f"{user1.id} successfuly unfollowed {user2.id}"}, status=status.HTTP_200_OK)
            except:
                activityStream.createActivity(user1.id,"unfollowed",user2.id,resolve(request.path_info).route,"UserUnfollow",False) 
                return Response({"message": "unfollow failed"}, status=status.HTTP_400_BAD_REQUEST)

        elif user2.isPrivate: # if private send request
            try:
                data['fromUser'] = user1.id
                data['toUser'] = user2.id

                if FollowRequest.objects.filter(fromUser=user1, toUser=user2).exists():
                    activityStream.createActivity(user1.id,"followed",user2.id,resolve(request.path_info).route,"UserFollow",False) 
                    return Response({"message": "request already sent"}, status=status.HTTP_400_BAD_REQUEST)
                
                serializer = FollowRequestSerializer(data=data)
                if serializer.is_valid():
                    serializer.save()
                    activityStream.createActivity(user1.id,"followed",user2.id,resolve(request.path_info).route,"UserFollow",True) 
                    return Response({"message": "successfuly sent request to private profile"}, status=status.HTTP_200_OK)
                else:
                    activityStream.createActivity(user1.id,"followed",user2.id,resolve(request.path_info).route,"UserFollow",False) 
                    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
            except:
                activityStream.createActivity(user1.id,"followed",user2.id,resolve(request.path_info).route,"UserFollow",False) 
                return Response({"message": "private profile follow failed"}, status=status.HTTP_400_BAD_REQUEST)
        
        else: #follow
            try:
                user1.followedUsers.add(user2)
                user2.followerUsers.add(user1)

                user1.save()
                user2.save()
                activityStream.createActivity(user1.id,"followed",user2.id,resolve(request.path_info).route,"UserFollow",True) 
                return Response({"message": f"{user1.id} successfuly followed {user2.id}"}, status=status.HTTP_200_OK)
            except:
                activityStream.createActivity(user1.id,"followed",user2.id,resolve(request.path_info).route,"UserFollow",False) 
                return Response({"message": "follow failed"}, status=status.HTTP_400_BAD_REQUEST)

class FollowRequests(GenericAPIView):
    """
    Returns a user's pending follow requests.
    Gets the user_id from the jwt key.
    """
    def get(self, request, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        user = User.objects.filter(id = user_id).first()

        pendingRequests = FollowRequest.objects.filter(toUser=user)

        requests = []
        try:
            for follow_request in pendingRequests.all():
                serializer = get_user(follow_request.fromUser)
                requests.append(serializer)
            activityStream.createActivity(user.id,"requested pending requests",user.id,resolve(request.path_info).route,"UserPendingRequest",True)
            return Response(requests, status=status.HTTP_200_OK)
        except:
            activityStream.createActivity(user.id,"requested pending requests",user.id,resolve(request.path_info).route,"UserPendingRequest",False)
            return Response(status=status.HTTP_400_BAD_REQUEST)

    
class AcceptFollowRequest(GenericAPIView):
    """
    Removes the pending request and adds the user to the follower list. 
    Returns a message and an HTTP status.
    """
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

        pendingRequest = FollowRequest.objects.filter(fromUser=user2, toUser=user1)

        if pendingRequest.count() != 0:
            try:
                user2.followedUsers.add(user1)
                user1.followerUsers.add(user2)

                user1.save()
                user2.save()

                pendingRequest.delete()
                activityStream.createActivity(user1.id,"accepted request from",user2.id,resolve(request.path_info).route,"UserAcceptRequest",True)
                return Response({"message": f"{user1.id} successfuly accepted the request from {user2.id}"}, status=status.HTTP_200_OK)
            except:
                activityStream.createActivity(user1.id,"accepted request from",user2.id,resolve(request.path_info).route,"UserAcceptRequest",False)
                return Response({"message": "request accept failed"}, status=status.HTTP_400_BAD_REQUEST)
        activityStream.createActivity(user1.id,"accepted request from",user2.id,resolve(request.path_info).route,"UserAcceptRequest",False)
        return Response({"message": f"no follow request from {user2.id}"}, status=status.HTTP_400_BAD_REQUEST)

class DeclineFollowRequest(GenericAPIView):
    """
    Removes the pending request. 
    Returns a message and an HTTP status.
    """
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

        pendingRequest = FollowRequest.objects.filter(fromUser=user2, toUser=user1)

        if pendingRequest.count() != 0:
            try:
                pendingRequest.delete()
                activityStream.createActivity(user1.id,"declined request from",user2.id,resolve(request.path_info).route,"UserDeclineRequest",True)
                return Response({"message": f"{user1.id} successfuly declined the request from {user2.id}"}, status=status.HTTP_200_OK)
            except:
                activityStream.createActivity(user1.id,"declined request from",user2.id,resolve(request.path_info).route,"UserDeclineRequest",False)
                return Response({"message": "request decline failed"}, status=status.HTTP_400_BAD_REQUEST)
        return Response({"message": f"no follow request from {user2.id}"}, status=status.HTTP_400_BAD_REQUEST)


class UserGet(GenericAPIView):
    """
    Returns the user with the given id. 
    If the requested user is public, it returns every field in user. If not, it returns every field except posts, savedPosts, likedPosts, and comments. 
    """
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
        if(not requested_user.isPrivate or requested_user in requester_user.followedUsers.all() or user_id==pk):
            serializer = dict(UserSerializer(requested_user).data)
            serializer = get_user(requested_user)
            activityStream.createActivity(requester_user.id,"requested user",requested_user.id,resolve(request.path_info).route,"UserRequest",True)
            return Response(serializer)
        else:
            if requested_user.isPrivate:
                serializer = dict(UserSerializer(requested_user).data)
                serializer.pop('posts')
                serializer.pop('savedPosts')
                serializer.pop('likedPosts')
                serializer.pop('comments')
                activityStream.createActivity(requester_user.id,"requested user",requested_user.id,resolve(request.path_info).route,"UserRequest",True)
                return Response(serializer, status=status.HTTP_200_OK)
            else: 
                activityStream.createActivity(requester_user.id,"requested user",requested_user.id,resolve(request.path_info).route,"UserRequest",False)
                return Response(status=status.HTTP_401_UNAUTHORIZED)

class ReportUser(GenericAPIView):
    """
    Creates a Report object from the jwt authorized user to the given user with id.
    Generates an email and sends it using Django's send_mail function. 
    """
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

        user = self.get_object(user_id)
        
        data = dict([])

        subject = "User Reported"
        content = f"User with id {user_id} reported user with id {pk}. \nVisit the website for more details: http://(our domain IP):8000/admin. \n\n- The Postory team"
    
    
        try:
            data['fromUser'] = user_id
            data['toUser'] = pk
            
            serializer = UserReportSerializer(data=data)
            if serializer.is_valid():
                serializer.save()
                send_mail(
                    subject,
                    content,
                    'from@example.com',
                    [x.email for x in User.objects.filter(is_superuser__in = [True]).all()], # send to all superusers
                    fail_silently=False,
                )
                activityStream.createActivity(user.id,"reported user",pk ,resolve(request.path_info).route,"UserReport",True)
                return Response({"message": "successfuly reported"}, status=status.HTTP_200_OK)
            else:
                activityStream.createActivity(user.id,"reported user",pk ,resolve(request.path_info).route,"UserReport",False)
                return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        except:
            activityStream.createActivity(user.id,"reported user",pk ,resolve(request.path_info).route,"UserReport",False)
            return Response({"message": "report failed"}, status=status.HTTP_400_BAD_REQUEST)

class ReportStory(GenericAPIView):
    """
    Creates a Report object from the jwt authorized user to the given story with id.
    Generates an email and sends it using Django's send_mail function. 
    """
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

        user = self.get_object(user_id)
        
        data = dict([])

        subject = "Story Reported"
        content = f"User with id {user_id} reported story with id {pk}. \nVisit the website for more details: http://(our domain IP):8000/admin. \n\n- The Postory team"
    
        try:
            data['fromStory'] = user_id
            data['toStory'] = pk
            serializer = StoryReportSerializer(data=data)
            if serializer.is_valid():
                serializer.save()
                send_mail(
                    subject,
                    content,
                    'from@example.com',
                    [x.email for x in User.objects.filter(is_superuser__in = [True]).all()], # send to all superusers
                    fail_silently=False,
                )
                activityStream.createActivity(user.id,"reported story",pk ,resolve(request.path_info).route,"StoryReport",True)
                return Response({"message": "successfuly reported"}, status=status.HTTP_200_OK)
            else:
                activityStream.createActivity(user.id,"reported story",pk ,resolve(request.path_info).route,"StoryReport",False)
                return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        except:
            activityStream.createActivity(user.id,"reported story",pk ,resolve(request.path_info).route,"StoryReport",False)
            return Response({"message": "report failed"}, status=status.HTTP_400_BAD_REQUEST)
        
class BanControl(GenericAPIView):
    """
    Checks whether the requested user is banned or not. 
    Returns the isBanned of the user object. 
    """
    def get(self, request, format=None):
        userid = request.auth['user_id']
        user = User.objects.get(id = userid)
        user.last_login = timezone.now()
        user.save()
        activityStream.createActivity(user.id,"'s ban controlled","ban",resolve(request.path_info).route,"BanControl",False)
        return Response({"isBanned": user.isBanned}, status=200)
        

def get_user(user):
    serializer = dict(UserSerializer(user).data)
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
        try:
            userPhoto = Image.objects.filter(user = user.id).first()
            temp['userPhoto'] = userPhoto.file.url
        except:
            temp['userPhoto'] = ""
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
        try:
            userPhoto = Image.objects.filter(user = user.id).first()
            temp['userPhoto'] = userPhoto.file.url
        except:
            temp['userPhoto'] = ""
        followed.append(temp)
    serializer['followerUsers'] = followers
    serializer['followedUsers'] = followed
    try:
        userPhoto = Image.objects.filter(user = user.id).first()
        serializer['userPhoto'] = userPhoto.file.url
    except:
        serializer['userPhoto'] = ""
    return serializer
