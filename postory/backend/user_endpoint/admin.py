from django.contrib import admin

from .models import *

class StoryReportAdmin(admin.ModelAdmin):
    readonly_fields = ('id',)

admin.site.register(StoryReport, StoryReportAdmin)
admin.site.register(User)
admin.site.register(UserReport)
admin.site.register(FollowRequest)