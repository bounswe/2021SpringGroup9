from django.shortcuts import render
from django.shortcuts import render
from django.http import HttpResponse
from django.http import HttpResponseServerError
from django.http import HttpResponseNotFound
from django.http import JsonResponse
import json
from ..models import Story
import requests
import environ
from rest_framework.decorators import api_view

env = environ.Env()
environ.Env.read_env('.env')
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
    

@api_view(['GET'])
def get_cityinfo(request, story_id):
  
   

    try:
        story = Story.objects.get(id=story_id)
    except Story.DoesNotExist:
        return HttpResponseNotFound(f"There is no Story object with story_id {story_id}!")

      
    
    try:
        url = "https://wft-geo-db.p.rapidapi.com/v1/geo/cities"

        

        locationinISOform=true_location_from(story.latitude, story.longitude)
        
        querystring = {"location":locationinISOform,"radius":"100", "minPopulation":"1000000" }

        headers = {
            'x-rapidapi-key': CITY_API_KEY,
            'x-rapidapi-host': "wft-geo-db.p.rapidapi.com"
            }

        response = requests.request("GET", url, headers=headers, params=querystring)

        
    except:
        return HttpResponseServerError("Could not establish connection")

    try:
        cityinfo =[ {
            
            "name": info["name"], "country": info["country"]
            
        }for info in response.json()["data"]]
        return JsonResponse(cityinfo, safe=False)
    except:
        return HttpResponseServerError("City not found.", status = 404)
    
    

