from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from api.models import Post
import json
import http.client, urllib.request, urllib.parse, urllib.error, base64


# This endpoint adds a post to the database.
# Also, using a third party api it will do semantic analysis for potential abuse.
@csrf_exempt 
def post_story(request):

    if request.method == 'GET':
        return HttpResponse('Frontend not yet implemented, you may use smth like Postman.')
    elif request.method != 'POST':
        return HttpResponse('Please use this end point with GET or POST.')

    # Seting various request parameters.
    request_body = json.loads(request.body)

    headers = {
        'Content-Type': 'application/json',
        'Ocp-Apim-Subscription-Key': 'e80d208310e94db58b7acb94254b3177',
    }

    tisane_body = json.dumps({ "language":"en", "content": request_body['story'], "settings":{} })

    try:
        conn = http.client.HTTPSConnection('api.tisane.ai')
        conn.request("POST", "/parse", tisane_body, headers)
        response = conn.getresponse()
        data = json.loads(response.read())

        if('abuse' in data):
            print(data['abuse'])

        #Create and save a post object. 
        #If there was some possible abuse in the text, set the notify_admin flag.
        Post(title = request_body['title'], story = request_body['story'], 
            location_long = request_body['long'], location_lat = request_body['lat'], 
            notify_admin = ('abuse' in data) ).save()
        conn.close()

        #If there was no error, just respond with OK (this is a POST request).
        return HttpResponse(status = 200)
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))
        return HttpResponse(status = 400)
    