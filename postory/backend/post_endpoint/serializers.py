from rest_framework import serializers

from .models import *

class PostSerializer(serializers.ModelSerializer):
    class Meta:
        model = Post
        fields = ['id', 'title', 'story', 'owner', 'tags', 'locations', 'images', 'postDate', 'editDate', 'storyDate', 'viewCount', 'comments', 'likeList']
        read_only_fields = []