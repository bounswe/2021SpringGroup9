from django.http.response import HttpResponse
from rest_framework.response import Response
from .models import Story, Translation
import requests
import environ
import json
from django.forms.models import model_to_dict
from django.http import  JsonResponse
from rest_framework import status
from rest_framework.decorators import api_view, renderer_classes

env = environ.Env()
environ.Env.read_env()


def retrieve_translation(story_id, target):
    """ Retrieves translation if it exists in the database."""
    try:
        translation = Translation.objects.get(story_id = story_id, language = target)
    except:
        return False        
    return translation

def check_story_length(title,body):
    """ Returns false for short stories. """
    if len(title) < 6 or len(title.split()) < 2:
        return False
    if len(body) < 10 or len(body.split()) < 2:  
        return False
    return True


@api_view(('GET',))
def translate(request, pk, target):
    """ Sends post request to IBM Watson Translator API. The source language is automatically detected. 
        The API may not be able to identify the source language if the string is too short (1 word)
        Returns a JSON of the translated story if the request is successful.
        target : language code, e.g. "de","es","tr"
    """
    try:
        story = Story.objects.get(pk=pk)
    except:
        return HttpResponse("Story does not exist.",status=status.HTTP_404_NOT_FOUND)

    story_dict = model_to_dict(story)
    check_translation = retrieve_translation(pk,target)
    if check_translation: 
        # translation is retrieved from the database
        story_dict['title'] = check_translation.title_trans
        story_dict['story'] = check_translation.story_trans
        return JsonResponse(story_dict,json_dumps_params={'ensure_ascii':False})
    
    else: # send request to IBM Language Translator API
        headers = {
            'Content-Type': 'application/json',
            'charset' : 'utf-8'
        }
        URL = 'https://api.eu-gb.language-translator.watson.cloud.ibm.com/instances/b2d14a2d-97d0-4b45-8f44-b3b993729db1'
        version = '2018-05-01'
        apikey = env('TRANSLATE_KEY')
        auth = ('apikey', apikey)

        if not check_story_length(story.title,story.story):
            return HttpResponse("Too short to translate.",status=status.HTTP_400_BAD_REQUEST)

        data = json.dumps({"text": [story.title,story.story],"target":target})    
        response = requests.post(f'{URL}/v3/translate?version={version}', headers=headers, data=data, auth=auth)
        
        if response.status_code == 200:
            response_json = response.json()
            title_trans = response_json['translations'][0]['translation'] # translation of title
            story_trans = response_json['translations'][1]['translation']     
            translation_dict = {"story_id":story, "language":target, "title_trans":title_trans,"story_trans":story_trans}
            new_translation = Translation.objects.create(**translation_dict) 
            new_translation.save() # save the new translation to database
            story_dict = model_to_dict(story)
            story_dict['title'] = title_trans 
            story_dict['story'] = story_trans
            return Response(story_dict)
        else :
            return HttpResponse(response.json()['error'],status=status.HTTP_404_NOT_FOUND) # return the error response of IBM Translator API
        
       