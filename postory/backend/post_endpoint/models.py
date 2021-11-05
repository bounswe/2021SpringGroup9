from django.db import models

class Post(models.Model):
    title = models.CharField(max_length=200)
    story = models.CharField(max_length=1000)
    owner = models.CharField(max_length=200)
    longitude = models.FloatField()
    latitude = models.FloatField()
    location = models.CharField(max_length=200)
    tags = models.CharField(max_length=200) # models.ListField
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
