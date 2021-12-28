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

from .models import Post, Location, Tag, Image, Comment
from .serializers import PostSerializer
from user_endpoint.models import User
from django.utils import timezone
import pytz

import requests
import json
import datetime
import environ
import jwt
from geopy import distance


class GetAllPosts(GenericAPIView):
    """
    Get all posts from the database. 
    """
    serializer_class = PostSerializer

    def get(self, request, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        user = User.objects.filter(id = user_id).first()
        if(user.isAdmin):
            posts = Post.objects.all()
            serializer = {}
            for story in posts:
                serializer[story.id] = get_story(story)
            return Response(serializer.values(), status=200)
        else:
            return Response(status = 401)

env = environ.Env(DEBUG=(bool, True))
environ.Env.read_env('.env')

class PostCreate(GenericAPIView):

    serializer_class = PostSerializer

    def post(self, request, format=None):
        userid = request.auth['user_id']
        data = dict(request.data)
        data['title'] = data['title'][0]
        data['story'] = data['story'][0]
        data['owner'] = userid
        if "year" in data:
            data['year'] = data['year'][0] + ',' + data['year'][1]
        if "month" in data:
            data['month'] = data['month'][0] + ',' + data['month'][1]
        if "day" in data:
            data['day'] = data['day'][0] + ',' + data['day'][1]
        if "hour" in data:
            data['hour'] = data['hour'][0] + ',' + data['hour'][1]
        if "minute" in data:
            data['minute'] = data['minute'][0] + ',' + data['minute'][1]

        
        images = data['images']
        imagesList = []
        for image in images:
            if image:
                imageObject = Image(file=image)
                imageObject.save()
                imagesList.append(imageObject)
        data['images'] = [image.id for image in imagesList]
         
        locations = data['locations']
        locationsList = []
        for location in locations:
            if location:
                location = json.loads(location)
                locationObject = Location(name=location["name"], coordsLatitude=location["latitude"], coordsLongitude=location["longitude"])
                locationsList.append(locationObject)
                locationObject.save()
        data['locations'] = [location.id for location in locationsList]

        tags = data['tags']
        tagsList = []
        for tag in tags:
            if tag:
                tag = str(tag).lower()
                search = Tag.objects.filter(content=tag).first()
                if(search):
                    tagsList.append(search)
                    continue
                tagObject = Tag(content=tag)
                tagObject.save()
                tagsList.append(tagObject)
        data['tags'] = [tag.id for tag in tagsList]
        user = User.objects.get(pk = userid)
        data['username'] = user.username
        serializer = PostSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            story = Post.objects.get(pk = serializer.data["id"])
            user.posts.add(story)
            return Response(get_story(story), status=200)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class PostListDetail(GenericAPIView):
    """
    Retrieve, update or delete a story instance.
    """

    def get_object(self, pk):
        try:
            return Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']

        user1 = User.objects.filter(id=user_id).first()
        story = self.get_object(pk)
        userid2 = story.owner
        user2 = User.objects.filter(pk=userid2).first()
        
        # only if the user is admin or is a follower of the post owner or it is the post owner or the post is public
        if not user2.isPrivate or user1.isAdmin or (user1 in user2.followerUsers.all()) or (user1.id == user2.id):
            serializer = get_story(story)
            return Response(serializer, status=200)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)
        
class PostUpdate(GenericAPIView):
    """
    Retrieve, update or delete a story instance.
    """

    def get_object(self, pk):
        try:
            return Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404
    
    def put(self, request, pk, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']

        user1 = User.objects.filter(id=user_id).first()
        story = self.get_object(pk)
        user2 = story.owner

        # update only if the user owns the post
        if (user1.id==user2):
            data = dict(request.data)
            data['title'] = data['title'][0]
            data['story'] = data['story'][0]
            data['owner'] = user_id
            if "year" in data:
                data['year'] = data['year'][0] + ',' + data['year'][1]
            if "month" in data:
                data['month'] = data['month'][0] + ',' + data['month'][1]
            if "day" in data:
                data['day'] = data['day'][0] + ',' + data['day'][1]
            if "hour" in data:
                data['hour'] = data['hour'][0] + ',' + data['hour'][1]
            if "minute" in data:
                data['minute'] = data['minute'][0] + ',' + data['minute'][1]
            
            images = data['images']
            imagesList = []
            for image in images:
                if image:
                    imageObject = Image(file=image)
                    imageObject.save()
                    imagesList.append(imageObject)
            data['images'] = [image.id for image in imagesList]
            
            locations = data['locations']
            locationsList = []
            for location in locations:
                if location:
                    location = json.loads(location)
                    locationObject = Location(name=location["name"], coordsLatitude=location["latitude"], coordsLongitude=location["longitude"])
                    locationsList.append(locationObject)
                    locationObject.save()
            data['locations'] = [location.id for location in locationsList]

            tags = data['tags']
            tagsList = []
            for tag in tags:
                if tag:
                    tag = str(tag).lower()
                    search = Tag.objects.filter(content=tag).first()
                    if(search):
                        tagsList.append(search)
                        continue
                    tagObject = Tag(content=tag)
                    tagObject.save()
                    tagsList.append(tagObject)
            data['tags'] = [tag.id for tag in tagsList]

            serializer = PostSerializer(story, data=data)
            if serializer.is_valid():
                serializer.save(editDate=timezone.now())
                story = self.get_object(pk)
                return Response(get_story(story), status=200)
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)
    
class PostDelete(GenericAPIView):
    """
    Retrieve, update or delete a story instance.
    """

    def get_object(self, pk):
        try:
            return Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404
    
    def delete(self, request, pk, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']

        story = self.get_object(pk)

        if(not user_id == story.owner):
            return Response(status = 401)
        story.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
    
class GetUsersPosts(GenericAPIView):
    def get(self, request, user_id, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        requester_user_id = decoded['user_id']

        requester_user = User.objects.filter(id = requester_user_id).first()
        requested_user = User.objects.filter(id = user_id).first()

        if(requested_user.id in requester_user.followedUsers.all() or not requested_user.isPrivate or requested_user.id == requester_user.id):
            posts = Post.objects.filter(owner = requested_user.id)
            serializer = {}
            for story in posts:
                serializer[story.id] = get_story(story)
            return Response(serializer.values(), status=200)
        else:
            return Response(status = 401)


class GetFollowedUsersPosts(GenericAPIView):
    def get(self,request,format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        user = User.objects.filter(id = user_id).first()
        followedUsers = user.followedUsers.all()
        posts = []
        for followedUser in followedUsers:
            followedUserPosts = Post.objects.filter(owner = followedUser.id)
            for post in followedUserPosts:
                posts.append(post)
        for post in Post.objects.filter(owner = user_id):
            posts.append(post)
        serializer = {}
        for story in posts:
            serializer[story.id] = get_story(story)
        return Response(serializer.values(), status=200)


class GetPostsDiscover(GenericAPIView):
    def get(self,request,format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        user = User.objects.filter(id = user_id).first()
        followedUsers = user.followedUsers.all()
        posts = set()
        for post in Post.objects.all():
            owner_of_post = User.objects.get(id=post.owner)
            if(not owner_of_post.isPrivate):
                posts.add(post)
        for followedUser in followedUsers:
            followedUserPosts = Post.objects.filter(owner = followedUser.id)
            for post in followedUserPosts:
                posts.add(post)
        serializer = {}
        for story in posts:
            serializer[story.id] = get_story(story)
        return Response(serializer.values(), status=200)

class CommentRequest(GenericAPIView):

    def get_object(self, pk):
        try:
            return Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404

    def post(self, request, pk, format=None):
        userid = request.auth['user_id']
        data = dict(request.data)
        
        comment = data['comment']
        if comment:
            commentObject = Comment(userid=userid, comment=comment)
            commentObject.save()
        else:
            return    

        story = self.get_object(pk)
        commentList = list(story.comments.all())
        commentList.append(commentObject.id)
        story.comments.set(commentList)
        
        try:
            story.save()
            return Response(get_story(story), status=200)
        except:
            return Response(status=status.HTTP_400_BAD_REQUEST)
        
class LikeRequest(GenericAPIView):

    def get_object(self, pk):
        try:
            return Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404

    def post(self, request, pk, format=None):
        userid = request.auth['user_id']

        story = self.get_object(pk)
        likeList = story.likeList.split(',')
        if likeList[0] == "":
            likeList = []
        if str(userid) in likeList:
            likeList.remove(str(userid))
        else:
            likeList.append(str(userid))
        story.likeList = ','.join(likeList)
        
        try:
            story.save()
            return Response(get_story(story), status=200)
        except:
            return Response(status=status.HTTP_400_BAD_REQUEST)
    

class nearbyStories(GenericAPIView):
    """
    
    """
    serializer_class = PostSerializer

    def get(self, request, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        user = User.objects.filter(id = user_id).first()
        data = dict(request.data)
        
        requestedDistance = float(data['distance'][0])
        pinPoint = json.loads(data['location'][0])
        pinPoint = [pinPoint["name"],pinPoint["latitude"],pinPoint["longitude"]]

        posts = Post.objects.all()
        serializer = {}
        for story in posts:
            story_instance = get_story(story)
            locations = story_instance['locations']
            toBeReturned = False
            for location in locations:
                distanceBetween = getDistanceBetween(location,pinPoint)
                if(distanceBetween < requestedDistance):
                    toBeReturned = True
                    break
            if(toBeReturned):
                serializer[story.id] = get_story(story)
        return Response(serializer.values(), status=200)


def get_story(story):
    username = story.username
    user = User.objects.filter(username = username).first()
    try:
        userPhoto = Image.objects.filter(user = user.id).first()
        userPhoto = userPhoto.file.url
    except:
        userPhoto = ""
    tags = []
    for tag in story.tags.all():
        tags.append(tag.content)
    locations = []
    for location in story.locations.all():
        temp = []
        temp.append(location.name)
        temp.append(location.coordsLatitude)
        temp.append(location.coordsLongitude)
        locations.append(temp)
    images = []
    for image in story.images.all():
        images.append(image.file.url)
    comments = []
    for comment in story.comments.all():
        temp = []
        temp.append(comment.userid)
        temp.append(User.objects.get(pk=comment.userid).username)
        temp.append(comment.comment)
        comments.append(temp)
    likeId = story.likeList.split(',')
    if likeId[0] == "":
        likeId = []
    likeId = [int(id) for id in likeId]
    likeList = []
    for id in likeId:
        temp = []
        temp.append(id)
        temp.append(User.objects.get(pk=id).username)
        likeList.append(temp)
    if story.year:
        year = [int(time) for time in story.year.split(',')]
    else:
        year = []
        
    if story.month:
        month = [int(time) for time in story.month.split(',')]
    else:
        month = []
        
    if story.day:
        day = [int(time) for time in story.day.split(',')]
    else:
        day = []
        
    if story.hour:
        hour = [int(time) for time in story.hour.split(',')]
    else:
        hour = []
        
    if story.minute:
        minute = [int(time) for time in story.minute.split(',')]
    else:
        minute = []
    serializer = dict(PostSerializer(story).data)
    serializer['tags'] = tags
    serializer['locations'] = locations
    serializer['images'] = images
    serializer['comments'] = comments
    serializer['likeList'] = likeList
    serializer['year'] = year
    serializer['month'] = month
    serializer['day'] = day
    serializer['hour'] = hour
    serializer['minute'] = minute
    serializer['username'] = username
    serializer['userPhoto'] = userPhoto
    return serializer


def getQuerySetOfNearby(data):
    requestedDistance = float(data['distance'][0])
    pinPoint = json.loads(data['location'][0])
    pinPoint = [pinPoint["name"],pinPoint["latitude"],pinPoint["longitude"]]
    posts = Post.objects.all()
    for story in posts:
        story_instance = get_story(story)
        locations = story_instance['locations']
        toBeReturned = False
        for location in locations:
            distanceBetween = getDistanceBetween(location,pinPoint)
            if(distanceBetween < requestedDistance):
                toBeReturned = True
                break
        if(not toBeReturned):
            posts.exclude(id = story_instance.id)
    return posts

def getDistanceBetween(loc1,loc2):
    lat1 = loc1[1]
    lon1 = loc1[2]
    lat2 = loc2[1]
    lon2 = loc2[2]
    return distance.distance((lat1,lon1),(lat2,lon2)).km