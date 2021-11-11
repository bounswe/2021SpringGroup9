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
from .serializers import PostSerializer

import requests
import json
import datetime
import environ

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
                images.append(image.url)
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
            imageObject = Image(file=image)
            imageObject.url = imageObject.file.url
            imagesList.append(imageObject)
            imageObject.save()
        data['images'] = [image.id for image in imagesList]
         
        locations = data['locations']
        locationsList = []
        for location in locations:
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
            return Response(serializer.data, status=200)
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
            images.append(image.url)
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
        story = self.get_object(pk)
        data = request.data
        locations = data['locations']
        locationsList = []
        for location in locations:
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
            return Response(serializer.data, status=200)
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
        story = self.get_object(pk)
        story.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
    
    

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