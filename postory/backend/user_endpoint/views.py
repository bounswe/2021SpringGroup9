from django.http import Http404
from django.http import HttpResponse
from drf_yasg.utils import swagger_auto_schema

from django.shortcuts import render
from rest_framework.generics import GenericAPIView
from rest_framework.response import Response
from rest_framework.decorators import api_view

from rest_framework import status
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView
from rest_framework.decorators import APIView

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
from drf_yasg import openapi

from django.contrib.auth import get_user_model
User = get_user_model()

class AddPhoto(APIView):
    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404

    param_photo = openapi.Parameter('photo file', openapi.IN_QUERY, description="profile photo", type=openapi.TYPE_FILE)

    image_schema = openapi.Schema(
        type=openapi.TYPE_OBJECT,
        properties={
            'file': openapi.Schema(type=openapi.TYPE_FILE, description='image file')
        },
        required=['file']
    )

    @swagger_auto_schema(
    method='post',
    request_body=image_schema,
    operation_description="Add profile photo",
    responses={200: openapi.Schema(type=openapi.TYPE_OBJECT, description="OK"), 400: openapi.Schema(type=openapi.TYPE_OBJECT, description="Bad Request")}
    )
    @api_view(['POST'])
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

class ChangePrivate(APIView):
    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except User.DoesNotExist:
            raise Http404

    @swagger_auto_schema(
    method='put',
    operation_description="Change privacy settings",
    responses={200: openapi.Schema(type=openapi.TYPE_OBJECT, description="OK"), 400: openapi.Schema(type=openapi.TYPE_OBJECT, description="Bad Request")}
    )
    @api_view(['PUT'])
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
        
class SearchUser(APIView):

    @swagger_auto_schema(
    method='post',
    operation_description="Search users",
    responses={200: UserSerializer(many=True)}
    )
    @api_view(['POST'])
    def post(self, request, term, format=None):
        userid = request.auth['user_id']
        users = User.objects.filter(username__contains=term)
        values = []
        for user in users:
            data = get_user(user)
            values.append(data)
        activityStream.createActivity(userid,"searched users","search",resolve(request.path_info).route,"SearchUser",True)
        return Response(values, status=200)
        
    
class UserFollowing(APIView):

    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except User.DoesNotExist:
            raise Http404
    
    @swagger_auto_schema(
    method='post',
    operation_description="Follow users or send follow request to users",
    responses={200: openapi.Schema(type=openapi.TYPE_OBJECT, description="OK"), 400: openapi.Schema(type=openapi.TYPE_OBJECT, description="Bad Request")}
    )
    @api_view(['POST'])
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

class FollowRequests(APIView):

    @swagger_auto_schema(
    method='post',
    operation_description="Get pending follow requests",
    responses={200: FollowRequestSerializer(many=True), 400: openapi.Schema(type=openapi.TYPE_OBJECT, description="Bad Request")}
    )
    @api_view(['POST'])
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

    
class AcceptFollowRequest(APIView):
    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except User.DoesNotExist:
            raise Http404

    @swagger_auto_schema(
    method='post',
    operation_description="Accept follow request", 
    responses={200: openapi.Schema(type=openapi.TYPE_OBJECT, description="OK"), 400: openapi.Schema(type=openapi.TYPE_OBJECT, description="Bad Request")}
    )
    @api_view(['POST'])
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

class DeclineFollowRequest(APIView):
    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except User.DoesNotExist:
            raise Http404

    @swagger_auto_schema(
    method='post',
    operation_description="Decline follow request",
    responses={200: openapi.Schema(type=openapi.TYPE_OBJECT, description="OK"), 400: openapi.Schema(type=openapi.TYPE_OBJECT, description="Bad Request")}
    )
    @api_view(['POST'])
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


class UserGet(APIView):
    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except User.DoesNotExist:
            raise Http404
    
    @swagger_auto_schema(
    method='get',
    operation_description="Get users",
    responses={200: UserSerializer, 401: openapi.Schema(type=openapi.TYPE_OBJECT, description="Unauthorized")}
    )
    @api_view(['GET'])
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

class ReportUser(APIView):
    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except User.DoesNotExist:
            raise Http404

    @swagger_auto_schema(
    method='post',
    operation_description="Report users",
    responses={200: openapi.Schema(type=openapi.TYPE_OBJECT, description="OK"), 400: openapi.Schema(type=openapi.TYPE_OBJECT, description="Bad Request")}
    )
    @api_view(['POST'])
    def post(self, request, pk, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']

        user = self.get_object(user_id)
        
        data = dict([])

        subject = "User Reported"
        content = f"User with id {user_id} reported user with id {pk}. \nVisit the website for more details: http://3.67.83.253:8000/admin. \n\n- The Postory team"
    
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

class ReportStory(APIView):
    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except User.DoesNotExist:
            raise Http404
    
    @swagger_auto_schema(
    method='post',
    operation_description="Report stories",
    responses={200: openapi.Schema(type=openapi.TYPE_OBJECT, description="OK"), 400: openapi.Schema(type=openapi.TYPE_OBJECT, description="Bad Request")}
    )
    @api_view(['POST'])
    def post(self, request, pk, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']

        user = self.get_object(user_id)
        
        data = dict([])

        subject = "Story Reported"
        content = f"User with id {user_id} reported story with id {pk}. \nVisit the website for more details: http://3.67.83.253:8000/admin. \n\n- The Postory team"
    
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
        
class BanControl(APIView):

    ban_schema_response = {
        status.HTTP_200_OK: openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
            'isBanned': openapi.Schema(type=openapi.TYPE_BOOLEAN)
            }
        )
    }

    @swagger_auto_schema(
    method='get',
    operation_description="Get the ban information of user",
    responses=ban_schema_response
    )
    @api_view(['GET'])
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
