from django.db import models

from backend.storage import ImageStorage

class Image(models.Model):
    uploadDate = models.DateTimeField(auto_now_add=True)
    file = models.FileField(storage=ImageStorage())

class Location(models.Model):
    name = models.CharField(max_length=200)
    coordsLatitude = models.FloatField()
    coordsLongitude = models.FloatField()

class Tag(models.Model):
    content = models.CharField(max_length=50)

class Post(models.Model):
    title = models.CharField(max_length=200)
    story = models.TextField()
    owner = models.CharField(max_length=200)
    locations = models.ManyToManyField(Location, blank=True)
    tags = models.ManyToManyField(Tag, blank=True)
    images = models.ManyToManyField(Image, blank=True)
    postDate = models.DateTimeField(auto_now_add=True)
    # comments = models.ListField
    editDate = models.DateTimeField(auto_now_add=True)
    storyDate = models.DateTimeField()
    viewCount = models.IntegerField(default=0)
    # likeList = models.ListField

class Comment(models.Model):
    pass

class Like(models.Model):
    pass
