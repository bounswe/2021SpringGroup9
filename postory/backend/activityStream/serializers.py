from rest_framework import serializers

from .models import *

class ActivityStreamSerializer(serializers.ModelSerializer):
    class Meta:
        model = ActivityStream
        fields = ['summary','actor','date','object','url','type','success']
        read_only_fields = []