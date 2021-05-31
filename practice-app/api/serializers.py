from rest_framework import serializers
from .models import Story

class StorySerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Story
        fields = ['id','date', 'user', 'tag', 'location', 'title', 'story']