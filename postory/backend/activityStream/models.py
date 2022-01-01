from django.db import models

class ActivityStream(models.Model):
    context = models.TextField(default="https://www.w3.org/ns/activitystreams")
    actor = models.IntegerField()
    summary = models.TextField()
    date = models.DateField(auto_now_add=True)
    object = models.TextField()
    url = models.TextField()
    type = models.TextField()
    success = models.BooleanField()
