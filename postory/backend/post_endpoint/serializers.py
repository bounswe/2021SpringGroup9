from rest_framework import serializers

from .models import *



class PostSerializer(serializers.ModelSerializer):
    class Meta:
        model = Post
        fields = ['title', 'story', 'owner', 'longitude','latitude', 'location', 'tags','postDate', 'editDate', 'storyDate', 'viewCount']
        read_only_fields = ['latitude', 'longitude']


