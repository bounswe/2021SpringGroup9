from django.db import models

from backend.storage import ImageStorage

class Image(models.Model):
    uploadDate = models.DateTimeField(auto_now_add=True)
    file = models.FileField(storage=ImageStorage())
    
    def __str__(self):
        return self.file.url

class Location(models.Model):
    name = models.CharField(max_length=200)
    coordsLatitude = models.FloatField()
    coordsLongitude = models.FloatField()
    
    def __str__(self):
        return self.name

class Comment(models.Model):
    userid = models.IntegerField()
    comment = models.TextField()
    
    def __str__(self):
        return self.comment

class Tag(models.Model):
    content = models.CharField(max_length=50)
    def __str__(self):
        return self.content

class Post(models.Model):
    title = models.CharField(max_length=200)
    story = models.TextField()
    owner = models.IntegerField()
    username = models.CharField(max_length=200, default="")
    locations = models.ManyToManyField(Location, blank=True)
    tags = models.ManyToManyField(Tag, blank=True)
    images = models.ManyToManyField(Image, blank=True)
    postDate = models.DateTimeField(auto_now_add=True)
    comments = models.ManyToManyField(Comment, blank=True)
    editDate = models.DateTimeField(auto_now_add=True)
    viewCount = models.IntegerField(default=0)
    likeList =  models.TextField(default="") 
    year = models.CharField(default="",max_length=20)
    month = models.CharField(default="",max_length=20)
    day = models.CharField(default="",max_length=20)
    hour = models.CharField(default="",max_length=20)
    minute = models.CharField(default="",max_length=20)
    
    def __str__(self):
        return self.title