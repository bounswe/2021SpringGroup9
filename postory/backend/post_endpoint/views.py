from django.http import Http404
from django.http import HttpResponse

from rest_framework import status
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView
from rest_framework.decorators import api_view

import urllib.parse as urlparse
from urllib.parse import urlencode

from .models import Post
from .serializers import PostSerializer

import requests
import json
import environ

class GetAllPosts(GenericAPIView):
    queryset = Post.objects.all()
    serializer_class = PostSerializer

    def get(self, request, format=None):
        if self.request.query_params is None:
            posts = Post.objects.all()
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)
        serializer = PostSerializer(posts, many=True)
        return Response(serializer.data)

class PostCreate(GenericAPIView):
    pass
