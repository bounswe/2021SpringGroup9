from django.contrib import admin

from .models import *


admin.site.register(User)
admin.site.register(UserReport)
admin.site.register(StoryReport)
admin.site.register(FollowRequest)