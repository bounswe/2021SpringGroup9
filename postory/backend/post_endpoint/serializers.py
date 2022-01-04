from rest_framework import serializers

from .models import *

class PostSerializer(serializers.ModelSerializer):
    class Meta:
        model = Post
        fields = ['id', 'title', 'story', 'owner', 'username', 'tags', 'locations', 'year', 'month', 'day', 'hour', 'minute', 'images', 'postDate', 'editDate', 'viewCount', 'comments', 'likeList']
        read_only_fields = []