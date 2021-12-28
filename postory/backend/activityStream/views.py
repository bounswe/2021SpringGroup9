from django.shortcuts import render

from .serializers import ActivityStreamSerializer

def createActivity(actor,action,object,url):
    data = dict()
    data['actor'] = actor
    data['action'] = action
    data['object'] = object
    data['url'] = url
    serializer = ActivityStreamSerializer(data=data)
    if(serializer.is_valid()):
        serializer.save()
    return

