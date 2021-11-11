from django.http import Http404
from django.http import HttpResponse

from rest_framework import status
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView
from rest_framework.decorators import api_view

import urllib.parse as urlparse
from urllib.parse import urlencode

from .models import Post, Location, Tag
from .serializers import PostSerializer

import requests
import json
import environ

class GetAllPosts(GenericAPIView):
    """
    Get all posts from the database. 
    """
    serializer_class = PostSerializer

    def get(self, request, format=None):
        posts = Post.objects.all()
        serializer = {}
        for story in posts:
            tags = []
            locations = []
            serializer[story.id] = dict(PostSerializer(story).data)
            for tag in story.tags.all():
                tags.append(tag.content)
            for location in story.locations.all():
                locations.append(location.name)
            serializer[story.id]['tags'] = tags
            serializer[story.id]['locations'] = locations
        return Response(serializer, status=200)

class PostCreate(GenericAPIView):
    pass