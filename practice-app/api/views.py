from django.shortcuts import render
from django.shortcuts import render
from django.http import HttpResponse
from django.http import HttpResponseServerError
from django.http import HttpResponseNotFound
from django.http import JsonResponse
import json
from .models import Story
import requests
import environ

env = environ.Env(DEBUG=(bool, False))
environ.Env.read_env()
CITY_API_KEY = env('CITY_API_KEY')

def true_location_from(latitude,longitude):    # in order to get locaiton in ISO form
    latlon=""
    if(latitude>=0):
        latlon=latlon+"+%f"%(latitude)
    elif(latitude<0):
        latlon=latlon+"%f"%(latitude)
    if(longitude>=0):
        latlon=latlon+"+%f"%(longitude)
    elif(longitude<0):
        latlon=latlon+"%f"%(longitude)
    return(latlon)    
    


def get_cityinfo(request, story_id):
  
   

    try:
        story = Story.objects.get(id=story_id)
    except Story.DoesNotExist:
        return HttpResponseNotFound(f"There is no Story object with story_id {story_id}!")

      
    
    try:
        url = "https://wft-geo-db.p.rapidapi.com/v1/geo/cities"

        

        locationinISOform=true_location_from(story.latitude, story.longitude)
        
        querystring = {"location":locationinISOform,"radius":"100" }

        headers = {
            'x-rapidapi-key': CITY_API_KEY,
            'x-rapidapi-host': "wft-geo-db.p.rapidapi.com"
            }

        response = requests.request("GET", url, headers=headers, params=querystring)

        info = json.loads(response.text)
    except:
        return HttpResponseServerError("Could not establish connection")

    try:
        cityinfo = {
            
            "name": info["data"][0]["name"],
            "country": info["data"][0]["country"],
            
        }
        return JsonResponse(cityinfo)
    except:
        return HttpResponseServerError("Error")

      
from django.http import Http404
from django.http import HttpResponse

from rest_framework import status
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView
from rest_framework.decorators import api_view

from .serializers import StorySerializer, LocationSerializer 
from .models import Story, Location

import urllib.parse as urlparse
from urllib.parse import urlencode
import requests
import json
import environ

env = environ.Env(DEBUG=(bool, False))
environ.Env.read_env()

class StoryList(GenericAPIView):
    """
    List stories.
    """ 
    queryset = Story.objects.all()
    serializer_class = StorySerializer

    def get(self, request, format=None):
        query_params = self.request.query_params
        if len(query_params) == 0:
            stories = Story.objects.all()     
        else:
            name = dict(query_params)['name'][0]
            stories = Story.objects.filter(name=name).all()
        serializer = StorySerializer(stories, many=True)
        return Response(serializer.data)


class StoryListDetail(GenericAPIView):
    """
    Retrieve, update or delete a story instance.
    """
    queryset = Story.objects.all()
    serializer_class = StorySerializer
    def get_object(self, pk):
        try:
            return Story.objects.get(pk=pk)
        except Story.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        story = self.get_object(pk)
        serializer = StorySerializer(story)
        return Response(serializer.data)

    def put(self, request, pk, format=None):
        story = self.get_object(pk)
        serializer = StorySerializer(story, data=request.data)
        if serializer.is_valid():
            if story.location != serializer.validated_data['location']:
                lat, lng = find_coordinates(serializer.validated_data['location'])
                serializer.save(latitude=lat, longitude=lng)
            else:
                serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        story = self.get_object(pk)
        story.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)

class Locations(GenericAPIView):
    """
    List locations of stories.
    """
    queryset = Location.objects.all()
    serializer_class = LocationSerializer

    def get(self, request, format=None):
        story = Story.objects.all()
        dic = []
        for s in story:
            location = s.location
            location = location.split(' ')
            location = "+".join(location)
            url = "https://maps.googleapis.com/maps/api/geocode/json"
            params = {"address":location, "key":env('GOOGLE_MAPS_API_KEY')}
            new_url = create_url(url,params)
            response = requests.get(new_url)
            json_data = json.loads(response.content)
            data = {'story_id': s.id,
                    'location_name' : json_data['results'][0]['formatted_address'], 
                    'location_latitude' : json_data['results'][0]['geometry']['location']['lat'], 
                    'location_longitude': json_data['results'][0]['geometry']['location']['lng']}
            dic.append(data)
        serializer = LocationSerializer(data=dic, many=True)
        if serializer.is_valid():
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class LocationDetail(GenericAPIView):
    """
    Retrieve location of a story instance.
    """
    queryset = Location.objects.all()
    serializer_class = LocationSerializer

    def get_object(self, pk):
        try:
            return Story.objects.get(pk=pk)
        except Story.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        story = self.get_object(pk)
        location = story.location
        location = location.split(' ')
        location = "+".join(location)
        url = "https://maps.googleapis.com/maps/api/geocode/json"
        params = {"address":location, "key":env('GOOGLE_MAPS_API_KEY')}
        new_url = create_url(url,params)
        response = requests.get(new_url)
        json_data = json.loads(response.content)
        data = {'story_id': story.id,
                'location_name' : json_data['results'][0]['formatted_address'], 
                'location_latitude' : json_data['results'][0]['geometry']['location']['lat'], 
                'location_longitude': json_data['results'][0]['geometry']['location']['lng']}
        serializer = LocationSerializer(data=data)
        if serializer.is_valid():
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
def locationMap(request, pk, format=None):
    """
    Retrieve location map of a story instance.
    """
    try:
        story = Story.objects.get(pk=pk)
    except Story.DoesNotExist:
        return HttpResponse(status = 404)
    location = story.location
    location = location.split(' ')
    location = "+".join(location)
    url = "https://maps.googleapis.com/maps/api/staticmap"
    params = {"markers":location, "center":location, "zoom":"13", "size":"800x400", "key":env('GOOGLE_MAPS_API_KEY')}
    new_url = create_url(url,params)
    response = requests.get(new_url)
    return HttpResponse(response.content, content_type="image/png", status = 200)


def create_url(url, params):
    url_parse = urlparse.urlparse(url)
    query = url_parse.query
    url_dict = dict(urlparse.parse_qsl(query))
    url_dict.update(params)
    url_new_query = urlparse.urlencode(url_dict)
    url_parse = url_parse._replace(query=url_new_query)
    new_url = urlparse.urlunparse(url_parse)
    return new_url

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
