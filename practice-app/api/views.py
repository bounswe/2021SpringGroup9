from django.http.response import HttpResponse
from rest_framework.response import Response
from .models import Story, Translation
import requests
import environ
import json
from rest_framework import status
from .serializers import *
from rest_framework.generics import GenericAPIView

env = environ.Env()
environ.Env.read_env()

class TranslationView(GenericAPIView):
    serializer_class = StorySerializer
    def retrieve_translation(story_id, target):
        """ Retrieves translation if it exists in the database."""
        try:
            translation = Translation.objects.get(story_id = story_id, language = target)
        except:
            return False        
        return translation

    def get(self, request, pk, target):
        try:
            story = Story.objects.get(pk=pk)
            story_dict = StorySerializer(story).data
        except:
            return Response("Story does not exist.",status=status.HTTP_404_NOT_FOUND)

        check_translation = TranslationView.retrieve_translation(pk,target)
        if check_translation: 
            # translation is retrieved from the database
            story_dict['title'] = check_translation.title_trans
            story_dict['story'] = check_translation.story_trans
            return Response(story_dict)
        else:
            new_trans = translate(story,target) # request translation from external API
            if 'error' in new_trans:
                return Response(new_trans['error'],new_trans['status'])
            else:
                story_dict['title'] = new_trans['title_trans']
                story_dict['story'] = new_trans['story_trans']     
                return Response(story_dict) # return translated story
            
def check_story_length(title,body):
    """ Returns false for short stories. """
    if len(title) < 6 or len(title.split()) < 2:
        return False
    if len(body) < 10 or len(body.split()) < 2:  
        return False
    return True


def translate(story, target):
    """ Sends post request to IBM Watson Translator API. The source language is automatically detected. 
        The API may not be able to identify the source language if the string is too short (1 word)
        Returns a JSON of the translated story if the request is successful.
        target : language code, e.g. "de","es","tr"
    """
    headers = {
        'Content-Type': 'application/json',
        'charset' : 'utf-8'
    }
    URL = 'https://api.eu-gb.language-translator.watson.cloud.ibm.com/instances/b2d14a2d-97d0-4b45-8f44-b3b993729db1'
    version = '2018-05-01'
    apikey = env('TRANSLATE_KEY')
    auth = ('apikey', apikey)

    if not check_story_length(story.title,story.story):
        return {'error':"Too short to translate.",'status' : status.HTTP_400_BAD_REQUEST}

    data = json.dumps({"text": [story.title,story.story],"target":target})    
    response = requests.post(f'{URL}/v3/translate?version={version}', headers=headers, data=data, auth=auth)
    
    if response.status_code == 200:
        response_json = response.json()
        title_trans = response_json['translations'][0]['translation'] # translation of title
        story_trans = response_json['translations'][1]['translation']         
        translated_fields = {'title_trans':title_trans, 'story_trans':story_trans}
        return translated_fields
    else :
       return {'error':response.json()['error'], 'status':status.HTTP_404_NOT_FOUND} # return the error response of IBM Translator API
    
       