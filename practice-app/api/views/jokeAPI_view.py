import requests
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse
from rest_framework import status    
from rest_framework.response import Response
import json
import environ
from rest_framework.decorators import api_view

# @csrf_exempt
@api_view(['GET'])
def joke(request,category):
    resp=requests.get("https://api.chucknorris.io/jokes/random?category=%s" % (category))
    if resp.status_code == 200:
        resp = resp.json()
        joke = resp["value"]
        return JsonResponse({
            'joke':joke
        })
    else: 
        return JsonResponse({
            'status' : 'non-existent'
        },status = 404) 
