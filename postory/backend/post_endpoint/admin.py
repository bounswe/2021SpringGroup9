from django.contrib import admin

from .models import *

"""
Models registered for admin.
"""
admin.site.register(Image)
admin.site.register(Location)
admin.site.register(Comment)
admin.site.register(Tag)
admin.site.register(Post)
