from django.shortcuts import render
from rest_framework.generics import GenericAPIView
from rest_framework.response import Response
from rest_framework import status
from .models import Location, Post, Tag
from .serializers import PostSerializer
import environ
import requests
import urllib.parse as urlparse
import json


env = environ.Env(DEBUG=(bool, True))
environ.Env.read_env('.env')

class PostCreate(GenericAPIView):

    serializer_class = PostSerializer

    def post(self, request, format=None):

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

        serializer = PostSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=200)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

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