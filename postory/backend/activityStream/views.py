from django.shortcuts import render

from .serializers import ActivityStreamSerializer

def createActivity(actor,summary,object,url,type,success):
    data = dict()
    data['actor'] = actor
    data['summary'] = summary
    data['object'] = object
    data['url'] = url
    data['type'] = type
    data['success'] = success
    serializer = ActivityStreamSerializer(data=data)
    if(serializer.is_valid()):
        serializer.save()
    return

