from django.db import models

class Post(models.Model):
    title = models.CharField(max_length=200)
    story = models.CharField(max_length=1000)
    username = models.CharField(max_length=32)
    tags = models.JSONField()

    def __str__(self):
        return self.title
