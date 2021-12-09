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
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']

        user1 = User.objects.filter(id=user_id).first()
        story = self.get_object(pk)
        username2 = story.owner
        user2 = User.objects.filter(username=username2).first()
        
        # only if the user is admin or is a follower of the post owner or it is the post owner
        if user1.isAdmin or (user1 in user2.follower.all()) or (user1.id == user2.id):
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
        else:
            return Response(status.HTTP_401_UNAUTHORIZED)
        
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
        username2 = story.owner
        user2 = User.objects.filter(username=username2).first()

        # update only if the user owns the post
        if (user1.id==user2.id):
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
        else:
            return Response(status.HTTP_401_UNAUTHORIZED)
    
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
        story = self.get_object(pk)
        story.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
    
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
    
def get_story(story):
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
    serializer = dict(PostSerializer(story).data)
    serializer['tags'] = tags
    serializer['locations'] = locations
    serializer['images'] = images
    serializer['comments'] = comments
    serializer['likeList'] = likeList
    return serializer
    

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