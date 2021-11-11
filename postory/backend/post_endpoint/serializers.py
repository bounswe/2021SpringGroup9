from rest_framework import serializers

from .models import *



class PostSerializer(serializers.ModelSerializer):
    class Meta:
        model = Post
        fields = ['id', 'title', 'story', 'owner', 'tags', 'locations', 'postDate', 'editDate', 'storyDate', 'viewCount']
        read_only_fields = []


