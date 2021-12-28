from django.db import models

class ActivityStream(models.Model):
    actor = models.IntegerField()
    action = models.TextField()
    date = models.DateField(auto_now_add=True)
    object = models.TextField()
    url = models.TextField()
