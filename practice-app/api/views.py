from django.http.response import HttpResponse
from django.shortcuts import render
import requests
from django.views.decorators.csrf import csrf_exempt
from .models import Story
from django.http import JsonResponse
import json
import environ

# Create your views here.
env = environ.Env(DEBUG=(bool, False))
environ.Env.read_env(env_file='../practice/.env')
WEATHER_API_KEY=env('WEATHER_API_KEY')

@csrf_exempt
def weather(request,story_id):
    if(request.method!='GET'):
        httpresponse=HttpResponse('Only GET method is available for this API')
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
    temperature=weather['main']['temp']
    feel=weather['main']['feels_like']
    wind=weather['wind']['speed']
    country=weather['sys']['country']
    timezone=weather['timezone']/3600
    return JsonResponse({
        'condition':condition,
        'temperature':round(temperature-(273.15),2),
        'feel':round(feel-(273.15),2),
        'wind':wind,
        'country':country,
        'time_zone':timezone
    })
