from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from api.models import Story
import json
import http.client, urllib.request, urllib.parse, urllib.error, base64


import environ

env = environ.Env(DEBUG=(bool, False))
environ.Env.read_env()
tisane_key = env('TISANE_API_KEY')

from rest_framework import status
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView
from rest_framework.decorators import api_view

# This endpoint adds a post to the database.
# Also, using a third party api it will do semantic analysis for potential abuse.
class StoryPost(GenericAPIView):
    """
    Create a story.
    """
    def post(self, request, format=None):
        # Seting various request parameters.
        request_body = json.loads(request.body)

        for field in ['title', 'story', 'name', 'longitude', 'latitude', 'location']:
            if field not in request_body.keys():
                return HttpResponse("Please add field %s in your request" % field, status = 400)

        headers = {
            'Content-Type': 'application/json',
            'Ocp-Apim-Subscription-Key': tisane_key,
        }

        tisane_body = json.dumps({ "language":"en", "content": request_body['story'], "settings":{} })

        try:
            conn = http.client.HTTPSConnection('api.tisane.ai')
            conn.request("POST", "/parse", tisane_body, headers)
            response = conn.getresponse()
            data = json.loads(response.read())
            conn.close()


            #Create and save a post object. 
            #If there was some possible abuse in the text, set the notifyAdmin flag.
            created_post = Story(title = request_body['title'], story = request_body['story'], name = request_body['name'], 
                longitude = request_body['longitude'], latitude = request_body['latitude'], location = request_body['location'], tag =" ", 
                notifyAdmin = ('abuse' in data) )
            created_post.save()

            #If there was no error, just respond with id.
            return JsonResponse(data = {'id' : created_post.id})
        except Exception as e:
            print("[Errno {0}] {1}".format(e.errno, e.strerror))
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
    