from django.http.response import HttpResponse
from django.shortcuts import render
import requests
from django.views.decorators.csrf import csrf_exempt
from ..models import Story
from django.http import JsonResponse
import json
import environ
from rest_framework.decorators import api_view

# Create your views here.
env = environ.Env()
environ.Env.read_env('.env')
WEATHER_API_KEY=env('WEATHER_API_KEY')

# @csrf_exempt
@api_view(['GET'])
def weather(request,story_id):

    if(request.method!='GET'):
        httpresponse=HttpResponse('Only GET method is available for this API')
        httpresponse.status_code=405
        return httpresponse

    if(not isinstance(story_id,int)):
        httpresponse=HttpResponse('Only integer values are used by this API')
        httpresponse.status_code=400
        return httpresponse

    try:
        story=Story.objects.get(id=story_id)
    except Story.DoesNotExist:
        httpresponse=HttpResponse('Story does not exist')
        httpresponse.status_code=404
        return httpresponse
        
    resp=requests.get("http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s" % (story.latitude,story.longitude,WEATHER_API_KEY))

    if(resp.status_code!=200):
        httpresponse=HttpResponse('Could not send request to OpenWeather API')
        httpresponse.status_code=400
        return httpresponse
    
    weather=resp.json()
    condition=weather['weather'][0]['main']
    temperature=round(weather['main']['temp']-(273.15),2)
    feel=round(weather['main']['feels_like']-(273.15),2)
    wind=weather['wind']['speed']
    country=weather['sys']['country']
    timezone=round(weather['timezone']/3600)
    


    if(temperature<0):
        comment="Wow it is freezing out there!"
    elif(temperature<15):
        comment="It is cold!"
    elif(temperature<30):
        comment="Temperatures are great! Go out there and have fun!"
    else:
        comment="It is hot! You sure you are OK?"

    return JsonResponse({
        'condition':condition,
        'temperature':temperature,
        'feel':feel,
        'wind':wind,
        'country':country,
        'time_zone':timezone,
        'comment':comment
    })
