from django.db.models.query_utils import Q
import requests

from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

import environ

from ..serializers import QuoteSerializer
from ..models import Story
from ..models import Quote

env = environ.Env(DEBUG=(bool, False))
environ.Env.read_env() 
QUOTE_API_KEY = env('QUOTE_API_KEY')

class GetQuoteTag(APIView):    
    """
    Get quotes that is tagged with the tag of the given story. 
    """    
    def get(self, request, pk):
        likemax = -1
        likeselect = -1
        story=Story.objects.get(id=pk)
        tag = story.tag
        param = {'filter': tag, 'type': "tag"}
        url = "https://favqs.com/api/quotes/"
        headers = {"Authorization": 'Token token="{}"'.format(QUOTE_API_KEY)}
        response = requests.get(url, headers=headers, params=param)
        quote = response.json()
        for i in range(len(quote['quotes'])):
            if quote['quotes'][i]['favorites_count'] > likemax: # get the quote with the most likes tagged with the story's tag in the Favqs API
                likemax = quote['quotes'][i]['favorites_count']
                likeselect = i
        q = quote['quotes'][likeselect]
        if q['body'] == 'No quotes found': # If a quote with that tag doesn't exist
            return Response(q['body']+" tagged with " + tag)
        
        qselect = {'id': q['id'],'Quote': q['body'], 'Author': q['author'], 'Likes': q['favorites_count']}
        return Response(qselect)
        

class GetQuoteLoc(APIView):
    """
    Get quotes that contains the location of the given story. 
    """    
    def get(self, request, pk):
        likemax = -1
        likeselect = -1
        story=Story.objects.get(id=pk)
        location = story.location
        param = {'filter': location}
        url = "https://favqs.com/api/quotes/"
        headers = {"Authorization": 'Token token="{}"'.format(QUOTE_API_KEY)}
        response = requests.get(url, headers=headers, params=param)
        quote = response.json()
        for i in range(len(quote['quotes'])):
            if quote['quotes'][i]['favorites_count'] > likemax: # get the quote with the most likes containing the location of the story
                likemax = quote['quotes'][i]['favorites_count']
                likeselect = i
        q = quote['quotes'][likeselect]
        if q['body'] == 'No quotes found': # if no quote contains the location name
            return Response(q['body']+" with location " + location)
        
        qselect = {'id': q['id'],'Quote': q['body'], 'Author': q['author'], 'Likes': q['favorites_count']}
        return Response(qselect)


class FavQuote(APIView):
    """
    Add the quote to the database if it is liked. Only the quotes that are retrieved from the Favqs API using a story can be posted.
    """
    def post(self, request, pk):
        quote = {}
        try:
            quote = Quote.objects.get(id=pk)
            quote.likes += 1  # if it is already in the database, increase the number of likes after post request
            quote.save()
            serializer = QuoteSerializer(quote)
            return Response(serializer.data, status=status.HTTP_200_OK)
        except Quote.DoesNotExist:
            url = "https://favqs.com/api/quotes/"+str(pk)
            headers={"Authorization": 'Token token="{}"'.format(QUOTE_API_KEY)}
            response = requests.get(url, headers=headers).json()

            if 'status' in response and response["status"] == 404:
                return Response(response, status=status.HTTP_404_NOT_FOUND)

            quote = {"id":response['id'], "author": response['author'], "body": response['body'], "likes": response['favorites_count']+1}
        
            serializer = QuoteSerializer(data=quote)
            if serializer.is_valid():
                serializer.save()
                return Response(serializer.data, status=status.HTTP_201_CREATED)
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

