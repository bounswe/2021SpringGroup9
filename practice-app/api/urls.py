from django.urls import include, path
from . import views

urlpatterns = [
    path('story/', views.post_story),
    path('flagged_stories/', views.flagged_stories)
]