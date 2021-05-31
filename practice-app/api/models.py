from django.db import models

# Create your models here.

class Story(models.Model):
    date = models.DateTimeField(auto_now_add=True)
    user = models.CharField(max_length=32)
    tag = models.CharField(max_length=200)
    location = models.CharField(max_length=200)
    title = models.CharField(max_length=200)
    story = models.CharField(max_length=1000)

    def __str__(self):
        return self.title
