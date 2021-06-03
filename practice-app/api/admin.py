from django.contrib import admin


from .models import Story,Translation
# Register your models here.

admin.site.register(Story)
admin.site.register(Translation)