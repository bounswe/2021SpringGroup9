from django.shortcuts import render
from django.http import HttpResponse, JsonResponse, Http404

from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

from .serializers import StorySerializer
from .models import Story
from rest_framework.renderers import JSONRenderer

import urllib.parse as urlparse
from urllib.parse import urlencode
import requests
import json

class StoryList(APIView):
    """
    List all snippets, or create a new snippet.
    """
    serializer_class = StorySerializer
    # parser_classes = [JSONParser, FormParser, MultiPartParser]
#     permission_classes = [permissions.IsAuthenticatedOrReadOnly]
#     renderer_classes = [JSONRenderer]

    def get(self, request, format=None):
        query_params = self.request.query_params
        if len(query_params) == 0:
            stories = Story.objects.all()     
        else:
            name = dict(query_params)['user'][0]
            stories = Story.objects.filter(user=name).all()
        serializer = StorySerializer(stories, many=True)
        return Response(serializer.data)

    def post(self, request, format=None):
        serializer = StorySerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class StoryListDetail(APIView):
    """
    Retrieve, update or delete a snippet instance.
    """
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
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        story = self.get_object(pk)
        story.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)

def location(request, format=None):
    story = Story.objects.all()
    dic = {}
    for s in story:
        location = s.location
        location = location.split(' ')
        location = "+".join(location)
        url = "https://maps.googleapis.com/maps/api/geocode/json"
        params = {"address":location, "key":"AIzaSyAD2l090z5SVkvr6hKxjwr-2h4jA-neLhA"}
        url_parse = urlparse.urlparse(url)
        query = url_parse.query
        url_dict = dict(urlparse.parse_qsl(query))
        url_dict.update(params)
        url_new_query = urlparse.urlencode(url_dict)
        url_parse = url_parse._replace(query=url_new_query)
        new_url = urlparse.urlunparse(url_parse)
        response = requests.get(new_url)
        json_data = json.loads(response.content)
        dic[s.id] = json_data
    return JsonResponse(dic)


def locationID(request, pk, format=None):
    story = Story.objects.get(pk=pk)
    location = story.location
    location = location.split(' ')
    location = "+".join(location)
    url = "https://maps.googleapis.com/maps/api/geocode/json"
    params = {"address":location, "key":"AIzaSyAD2l090z5SVkvr6hKxjwr-2h4jA-neLhA"}
    url_parse = urlparse.urlparse(url)
    query = url_parse.query
    url_dict = dict(urlparse.parse_qsl(query))
    url_dict.update(params)
    url_new_query = urlparse.urlencode(url_dict)
    url_parse = url_parse._replace(query=url_new_query)
    new_url = urlparse.urlunparse(url_parse)
    response = requests.get(new_url)
    json_data = json.loads(response.content)
    return JsonResponse({story.id:json_data})

def locationMap(request, pk, format=None):
    story = Story.objects.get(pk=pk)
    location = story.location
    location = location.split(' ')
    location = "+".join(location)
    url = "https://maps.googleapis.com/maps/api/staticmap"
    params = {"markers":location, "center":location, "zoom":"13", "size":"800x400", "key":"AIzaSyAD2l090z5SVkvr6hKxjwr-2h4jA-neLhA"}
    url_parse = urlparse.urlparse(url)
    query = url_parse.query
    url_dict = dict(urlparse.parse_qsl(query))
    url_dict.update(params)
    url_new_query = urlparse.urlencode(url_dict)
    url_parse = url_parse._replace(query=url_new_query)
    new_url = urlparse.urlunparse(url_parse)
    response = requests.get(new_url)
    return HttpResponse(response.content, content_type="image/png")