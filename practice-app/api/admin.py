from django.contrib import admin

from .models import Story
from .models import Quote

admin.site.register(Story)
admin.site.register(Quote)
