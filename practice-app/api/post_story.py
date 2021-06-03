from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from api.models import Story
import json
import http.client, urllib.request, urllib.parse, urllib.error, base64


import environ

env = environ.Env(DEBUG=(bool, False))
environ.Env.read_env()
tisane_key = env('TISANE_API_KEY')



# This endpoint adds a post to the database.
# Also, using a third party api it will do semantic analysis for potential abuse.
@csrf_exempt 
def post_story(request):

    if request.method != 'POST':
        return HttpResponse('Please use this end point with POST.', status = 400)

    # Seting various request parameters.
    request_body = json.loads(request.body)

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
            longitude = request_body['long'], latitude = request_body['lat'], location = request_body['location'], tag =" ", 
            notifyAdmin = ('abuse' in data) )
        created_post.save()

        #If there was no error, just respond with id.
        return JsonResponse(data = {'id' : created_post.id})
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))
        return HttpResponse(status = 400)

# Gets flagged stories (notifyAdmin == True) from the database.
# Only used with get.
def flagged_stories(request):
    if request.method != 'GET':
        return HttpResponse('Please use this end point with GET.')
    query = Story.objects.filter(notifyAdmin = True)
    response = {'flagged_stories': []}
    for q in query:
        response['flagged_stories'].append({
            'id': q.id,
            'title': q.title,
            'story': q.story,
        })
    return JsonResponse(data = response)
    