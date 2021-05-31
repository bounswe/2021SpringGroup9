from django.shortcuts import render
import requests
from django.views.decorators.csrf import csrf_exempt
from "./models.py" import Post
import json

# Create your views here.

@csrf_exempt
def weather(request,post_id):
    post=Post.objects.filter(id__exact=post_id).get()
    resp=requests.get("http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s" % (post.latitude,post.longitude,"168f38507b873881df14be6e27c4326c"))
    weather=resp.json()
    condition=weather['weather'][0]['main']
    temperature=weather['main']['temp']
    feel=weather['main']['feels_like']
    wind=weather['wind']['speed']
    country=weather['sys']['country']
    timezone=weather['timezone']/3600
    return json.dumps({
        'condition':condition,
        'temperature':temperature-(273.15),
        'feel':feel-(273.15),
        'wind':wind,
        'country':country,
        'time_zone':timezone
    })
