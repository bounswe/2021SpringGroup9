from django.db.models.query_utils import Q
import requests

from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

from django.http import Http404
import environ

from .serializers import QuoteSerializer
from .models import Story
from .models import Quote

env = environ.Env(DEBUG=(bool, False))
environ.Env.read_env() 
QUOTE_API_KEY = env('QUOTE_API_KEY')

class GetQuote(APIView):        
    def get(self, request, pid):
        likemax = -1
        likeselect = -1
        post=Story.objects.get(id=pid)
        tag = post.tag
        param = {'filter': tag}
        url = "https://favqs.com/api/quotes/"
        headers = {"Authorization": 'Token token="{}"'.format(QUOTE_API_KEY)}
        response = requests.get(url, headers=headers, params=param)
        quote = response.json()
        for i in range(len(quote['quotes'])):
            if quote['quotes'][i]['favorites_count'] > likemax:
                likemax = quote['quotes'][i]['favorites_count']
                likeselect = i
        q = quote['quotes'][likeselect]
        qselect = {'id': q['id'],'Quote': q['body'], 'Author': q['author'], 'Likes': q['favorites_count']}
        return Response(qselect)

class FavQuote(APIView):
    def post(self, request, pk):
        quote = {}
        try:
            quote = Quote.objects.get(id=pk)
            quote.likes += 1
            quote.save()
            serializer = QuoteSerializer(quote)
            return Response(serializer.data, status=status.HTTP_200_OK)
        except Quote.DoesNotExist:
            url = "https://favqs.com/api/quotes/"+str(pk)
            headers={"Authorization": 'Token token="{}"'.format(QUOTE_API_KEY)}
            response = requests.get(url, headers=headers).json()
            print(response)

            if 'status' in response and response["status"] == 404:
                return Response(response, status=status.HTTP_404_NOT_FOUND)

            quote = {"id":response['id'], "author": response['author'], "body": response['body'], "likes": response['favorites_count']+1}
        
            serializer = QuoteSerializer(data=quote)
            if serializer.is_valid():
                serializer.save()
                return Response(serializer.data, status=status.HTTP_201_CREATED)
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

