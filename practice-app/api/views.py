from django.shortcuts import render
import requests
from django.views.decorators.csrf import csrf_exempt
from .models import Post
from django.http import JsonResponse
import json
import environ

# Create your views here.
env = environ.Env(DEBUG=(bool, False))
environ.Env.read_env()
WEATHER_API_KEY=env('WEATHER_API_KEY')

@csrf_exempt
def weather(request,post_id):
    post=Post.objects.get(id=post_id)
    resp=requests.get("http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s" % (post.latitude,post.longitude,WEATHER_API_KEY))
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
