from django.db import models

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
    locations = models.ManyToManyField(Location)
    tags = models.ManyToManyField(Tag)
    postDate = models.DateTimeField(auto_now_add=True)
    # multimedia 
    # comments = models.ListField
    editDate = models.DateTimeField(auto_now_add=True)
    storyDate = models.DateTimeField()
    viewCount = models.IntegerField(default=0)
    # likeList = models.ListField

class Comment(models.Model):
    pass

class Like(models.Model):
    pass