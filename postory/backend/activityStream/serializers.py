from rest_framework import serializers

from .models import *

class ActivityStreamSerializer(serializers.ModelSerializer):
    class Meta:
        model = ActivityStream
        fields = ['action','actor','date','object','url']
        read_only_fields = []