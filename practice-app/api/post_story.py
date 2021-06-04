from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from api.models import Story
import json
import http.client, urllib.request, urllib.parse, urllib.error, base64
from rest_framework import status
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView
from rest_framework.decorators import api_view
from .serializers import StorySerializer
import urllib.parse as urlparse
from urllib.parse import urlencode
import requests

import environ

env = environ.Env(DEBUG=(bool, False))
environ.Env.read_env()
tisane_key = env('TISANE_API_KEY')


# This endpoint adds a post to the database.
# Also, using a third party api it will do semantic analysis for potential abuse.
class StoryPost(GenericAPIView):
    queryset = Story.objects.all()
    serializer_class = StorySerializer
    def post(self, request, format=None):
        serializer = StorySerializer(data=request.data)
        if serializer.is_valid():
            lat, lng = find_coordinates(serializer.validated_data['location'])
            abuse = check_abuse(serializer.validated_data['story'], serializer)
            if abuse != True and abuse != False:
                return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
            serializer.save(notifyAdmin = abuse, latitude=lat, longitude=lng)
            return Response(serializer.data, status=200)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        

def check_abuse(story, serializer):
    #Using a third party api, check if there is possible abuse in text.
    #If runs correctly will return a boolean variable denoting abuse.
    #If exception occurs return a HttpResponse of 400.

    # Seting various request parameters.
    headers = {
        'Content-Type': 'application/json',
        'Ocp-Apim-Subscription-Key': tisane_key,
    }

    tisane_body = json.dumps({ "language":"en", "content": story, "settings":{} })

    try:
        conn = http.client.HTTPSConnection('api.tisane.ai')
        conn.request("POST", "/parse", tisane_body, headers)
        response = conn.getresponse()
        data = json.loads(response.read())
        conn.close()
        
        return 'abuse' in data
    except Exception as e:
        print(e)
        return HttpResponse(e.strerror, status = 400)


# Gets flagged stories (notifyAdmin == True) from the database.
# Only used with get.
def flagged_stories(request):
    if request.method != 'GET':
        return HttpResponse('Please use this end point with GET.', 400)
    query = Story.objects.filter(notifyAdmin = True)
    response = {'flagged_stories': []}
    for q in query:
        response['flagged_stories'].append({
            'id': q.id,
            'title': q.title,
            'story': q.story,
        })
    return JsonResponse(data = response)






# This part taken from @melihaydogd.

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