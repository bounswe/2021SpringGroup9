from rest_framework import serializers

from .models import *



class StorySerializer(serializers.ModelSerializer):
    class Meta:
        model = Story
        fields = ['id' ,'date', 'name', 'location', 'tag', 'title', 'story', 'notifyAdmin','latitude', 'longitude']
        read_only_fields = ['notifyAdmin','latitude', 'longitude'] 



class QuoteSerializer(serializers.ModelSerializer):
    class Meta:
        model = Quote
        fields = ['id', 'author', 'body', 'likes']

class LocationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Location
        fields = ['story_id', 'location_name', 'location_latitude', 'location_longitude']


