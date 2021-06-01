from django.db import models

# Create your models here.

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
