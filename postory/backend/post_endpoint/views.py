from django.shortcuts import render
from rest_framework.generics import GenericAPIView
from rest_framework.response import Response
from rest_framework import status
from .models import Post
from .serializers import PostSerializer
import environ
import requests
import urllib.parse as urlparse
import json


env = environ.Env(DEBUG=(bool, True))
environ.Env.read_env('.env')

class PostCreate(GenericAPIView):
    
    queryset = Post.objects.all()
    serializer_class = PostSerializer

    def post(self, request, format=None):
        serializer = PostSerializer(data=request.data)
        if serializer.is_valid():
            try:
                lat, lng = find_coordinates(serializer.validated_data['location'])
            except:
                return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
            print(serializer.validated_data['location'])
            serializer.save(latitude=lat,longitude=lng)
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
    print(json_data)
    location = json_data['results'][0]['geometry']['location']
    print(location['lat'],location['lng'])
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