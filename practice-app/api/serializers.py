from rest_framework import serializers
from .models.model_translationAPI_niyazi import *

class StorySerializer(serializers.ModelSerializer):
    class Meta:
        model = Story
        fields = ['id' ,'date', 'name', 'location', 'tag', 'title', 'story', 'notifyAdmin','latitude', 'longitude']
        read_only_fields = ['notifyAdmin','latitude', 'longitude']

    