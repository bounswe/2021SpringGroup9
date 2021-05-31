from django.db import models

# Create your models here.
from django.db import models

# Create your models here.
class Post(models.Model):
    title = models.CharField(max_length = 30)
    story = models.CharField(max_length = 2500)
    location_long = models.FloatField()
    location_lat = models.FloatField()
    notify_admin = models.BooleanField(default = False)
    def __str__(self):
        return "%s Where: %f %f \n\n %s" % (self.title, self.location_long, self.location_lat, self.story)