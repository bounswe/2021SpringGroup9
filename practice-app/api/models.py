import datetime
from django.db import models
from django.db.models.fields.related import ForeignKey

class Quote(models.Model):
    id = models.IntegerField(primary_key=True)
    author = models.CharField(max_length=255)
    body = models.CharField(max_length=2047)
    likes = models.IntegerField()

    def __str__(self):
        return self.body


class Story(models.Model):
    title = models.CharField(max_length=200)
    story = models.CharField(max_length=1000)
    name = models.CharField(max_length=200)
    longitude = models.FloatField()
    latitude = models.FloatField()
    location = models.CharField(max_length=200)
    tag = models.CharField(max_length=200)
    date = models.DateTimeField(auto_now_add=True)
    notifyAdmin = models.BooleanField(default=False)

    def __str__(self):
        return self.title


class Location(models.Model):
    story_id = models.IntegerField()
    location_name = models.CharField(max_length=200)
    location_longitude = models.FloatField()
    location_latitude = models.FloatField()
    
    class Meta:
        managed = False