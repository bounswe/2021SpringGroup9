from django.db.models.query_utils import Q
import requests

from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

import environ

from ..serializers import QuoteSerializer
from ..models import Story
from ..models import Quote

env = environ.Env()
environ.Env.read_env('.env')
QUOTE_API_KEY = env('QUOTE_API_KEY')


def no_quote_helper(param):

    url ="https://favqs.com/api/qotd"
    headers = {"Authorization": 'Token token="{}"'.format(QUOTE_API_KEY)}

    try:
        response = requests.get(url, headers=headers, params=param)
        quote = response.json()
        q = quote['quote']
        return q
    except: 
        return "No quotes found"


def most_like_quote_helper(quote):
    likemax = -1
    likeselect = -1

    for i in range(len(quote['quotes'])):
        if quote['quotes'][i]['favorites_count'] > likemax: # get the quote with the most likes tagged with the story's tag in the Favqs API
            likemax = quote['quotes'][i]['favorites_count']
            likeselect = i

    return quote['quotes'][likeselect]

    
class GetQuoteTag(APIView):    
    """
    Get quotes that are tagged with the tag of the given story. 
    """    
    def get(self, request, pk):

        try:
            story=Story.objects.get(pk=pk)
        except: 
            return Response(status=status.HTTP_400_BAD_REQUEST)
        tag = story.tag

        param = {'filter': tag.lower(), 'type': "tag"}
        url = "https://favqs.com/api/quotes/"
        headers = {"Authorization": 'Token token="{}"'.format(QUOTE_API_KEY)}

        try:
            response = requests.get(url, headers=headers, params=param)
            quote = response.json()

            q = most_like_quote_helper(quote)

            if q['body'] == 'No quotes found': # If a quote with that tag doesn't exist return random quote
                q = no_quote_helper(param)
                if q == "No quotes found": # If no random quote is returned
                    raise Exception(q)
            
            qselect = {'id': q['id'],'Quote': q['body'], 'Author': q['author'], 'Likes': q['favorites_count']}
            return Response(qselect, status=status.HTTP_200_OK)
        except:
            return Response(response.status)
        

class GetQuoteLoc(APIView):
    """
    Get quotes that contain the location of the given story. 
    """    
    def get(self, request, pk):

        try:
            story=Story.objects.get(pk=pk)
        except:
            return Response(status=status.HTTP_400_BAD_REQUEST)

        location = story.location
        param = {'filter': location}
        url = "https://favqs.com/api/quotes/"
        headers = {"Authorization": 'Token token="{}"'.format(QUOTE_API_KEY)}
        try:
            response = requests.get(url, headers=headers, params=param)
            quote = response.json()
        
            q = most_like_quote_helper(quote)

            if q['body'] == 'No quotes found': # if no quote contains the location name return random quote
                q = no_quote_helper(param)
                if q == "No quotes found": # If no random quote is returned
                    raise Exception(q)
            
            qselect = {'id': q['id'],'Quote': q['body'], 'Author': q['author'], 'Likes': q['favorites_count']}
            return Response(qselect, status=status.HTTP_200_OK)
        except:
            return Response(response.status)


class FavQuote(APIView):
    """
    Add the quote to the database if it is liked. Only the quotes that are retrieved from the Favqs API can be posted.
    """
    def post(self, request, pk):
        quote = {}
        try:
            quote = Quote.objects.get(pk=pk)
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