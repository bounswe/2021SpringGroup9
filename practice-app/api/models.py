from django.db import models

# Create your models here.

class Post(models.Model):
    id=models.IntegerField(primary_key=True)
    title=models.CharField(max_length=200)
    story=models.CharField(max_length=1000)
    latitude=models.FloatField()
    longitude=models.FloatField()

    def __str__(self):
        return self.title