from .models import Story
from .models import Quote
from rest_framework import serializers


class StorySerializer(serializers.ModelSerializer):
    class Meta:
        model = Story
        fields = ['title', 'story', 'name', 'longitude', 'latitude', 'location', 'tag', 'date', 'notifyAdmin']

class QuoteSerializer(serializers.ModelSerializer):
    class Meta:
        model = Quote
        fields = ['id', 'author', 'body', 'likes']


