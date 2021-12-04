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

from .models import Post, Location, Tag, Image
from user_endpoint.models import User
from .serializers import PostSerializer

import requests
import json
import datetime
import environ
import jwt

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
                tags = []
                locations = []
                images = []
                serializer[story.id] = dict(PostSerializer(story).data)
                for tag in story.tags.all():
                    tags.append(tag.content)
                for location in story.locations.all():
                    values = []
                    values.append(location.name)
                    values.append(location.coordsLatitude)
                    values.append(location.coordsLongitude)
                    locations.append(values)
                for image in story.images.all():
                    images.append(image.file.url)
                serializer[story.id]['tags'] = tags
                serializer[story.id]['locations'] = locations
                serializer[story.id]['images'] = images
            return Response(serializer.values(), status=200)
        else:
            return Response(status = 401)

env = environ.Env(DEBUG=(bool, True))
environ.Env.read_env('.env')

class PostCreate(GenericAPIView):

    serializer_class = PostSerializer

    def post(self, request, format=None):

        data = dict(request.data)
        data['title'] = data['title'][0]
        data['story'] = data['story'][0]
        data['owner'] = data['owner'][0]
        data['storyDate'] = data['storyDate'][0]
        
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
                location = str(location).lower() 
                search = Location.objects.filter(name=location).first()
                if(search):
                    locationsList.append(search)
                    continue
                name = location
                coordsLatitude, coordsLongitude = find_coordinates(location)
                locationObject = Location(name=name, coordsLatitude=coordsLatitude, coordsLongitude=coordsLongitude)
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

        serializer = PostSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            tags = []
            for tag in tagsList:
                tags.append(tag.content)
            locations = []
            for location in locationsList:
                temp = []
                temp.append(location.name)
                temp.append(location.coordsLatitude)
                temp.append(location.coordsLongitude)
                locations.append(temp)
            images = []
            for image in imagesList:
                images.append(image.file.url)
            serializer = dict(serializer.data)
            serializer['tags'] = tags
            serializer['locations'] = locations
            serializer['images'] = images
            return Response(serializer, status=200)
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
        story = self.get_object(pk)
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
        serializer = dict(PostSerializer(story).data)
        serializer['tags'] = tags
        serializer['locations'] = locations
        serializer['images'] = images
        return Response(serializer)
    
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

        story = self.get_object(pk)
        data = dict(request.data)
        data['title'] = data['title'][0]
        data['story'] = data['story'][0]
        data['owner'] = data['owner'][0]
        data['storyDate'] = data['storyDate'][0]

        if(not user_id == data['owner']):
            return Response(status = 401)
        
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
                location = str(location).lower() 
                search = Location.objects.filter(name=location).first()
                if(search):
                    locationsList.append(search)
                    continue
                name = location
                coordsLatitude, coordsLongitude = find_coordinates(location)
                locationObject = Location(name=name, coordsLatitude=coordsLatitude, coordsLongitude=coordsLongitude)
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
            serializer.save(editDate=datetime.datetime.now())
            tags = []
            for tag in tagsList:
                tags.append(tag.content)
            locations = []
            for location in locationsList:
                temp = []
                temp.append(location.name)
                temp.append(location.coordsLatitude)
                temp.append(location.coordsLongitude)
                locations.append(temp)
            images = []
            for image in imagesList:
                images.append(image.file.url)
            serializer = dict(serializer.data)
            serializer['tags'] = tags
            serializer['locations'] = locations
            serializer['images'] = images
            return Response(serializer, status=200)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    
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
    def get(self, request, username, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']

        requester_user = User.objects.filter(id = user_id).first()
        requested_user = User.objects.filter(username = username).first()

        if(requested_user.id in requester_user.followedUsers.all() or not requested_user.isPrivate):
            posts = Post.objects.filter(owner = requested_user.id)
            serializer = {}
            for story in posts:
                tags = []
                locations = []
                images = []
                serializer[story.id] = dict(PostSerializer(story).data)
                for tag in story.tags.all():
                    tags.append(tag.content)
                for location in story.locations.all():
                    values = []
                    values.append(location.name)
                    values.append(location.coordsLatitude)
                    values.append(location.coordsLongitude)
                    locations.append(values)
                for image in story.images.all():
                    images.append(image.file.url)
                serializer[story.id]['tags'] = tags
                serializer[story.id]['locations'] = locations
                serializer[story.id]['images'] = images
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
            followedUserPosts = Post.objects.filter(owner = followedUser)
            posts.append(followedUserPosts)
        serializer = {}
        for story in posts:
            tags = []
            locations = []
            images = []
            serializer[story.id] = dict(PostSerializer(story).data)
            for tag in story.tags.all():
                tags.append(tag.content)
            for location in story.locations.all():
                values = []
                values.append(location.name)
                values.append(location.coordsLatitude)
                values.append(location.coordsLongitude)
                locations.append(values)
            for image in story.images.all():
                images.append(image.file.url)
            serializer[story.id]['tags'] = tags
            serializer[story.id]['locations'] = locations
            serializer[story.id]['images'] = images
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
            followedUserPosts = Post.objects.filter(owner = followedUser)
            posts.add(followedUserPosts)
        
        serializer = {}
        for story in posts:
            tags = []
            locations = []
            images = []
            serializer[story.id] = dict(PostSerializer(story).data)
            for tag in story.tags.all():
                tags.append(tag.content)
            for location in story.locations.all():
                values = []
                values.append(location.name)
                values.append(location.coordsLatitude)
                values.append(location.coordsLongitude)
                locations.append(values)
            for image in story.images.all():
                images.append(image.file.url)
            serializer[story.id]['tags'] = tags
            serializer[story.id]['locations'] = locations
            serializer[story.id]['images'] = images
        return Response(serializer.values(), status=200)


def find_coordinates(location):
    location = location.split(' ')
    location = "+".join(location)
    url = "https://maps.googleapis.com/maps/api/geocode/json"
    params = {"address":location, "key":env('GOOGLE_MAPS_API_KEY')}
    new_url = create_url(url,params)
    response = requests.get(new_url)
    json_data = json.loads(response.content)
    location = json_data['results'][0]['geometry']['location']
    return location['lat'], location['lng']

def create_url(url, params):
    url_parse = urlparse.urlparse(url)
    query = url_parse.query
    url_dict = dict(urlparse.parse_qsl(query))
    url_dict.update(params)
    url_new_query = urlparse.urlencode(url_dict)
    url_parse = url_parse._replace(query=url_new_query)
    new_url = urlparse.urlunparse(url_parse)
    return new_url