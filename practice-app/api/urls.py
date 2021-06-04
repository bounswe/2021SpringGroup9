from django.urls import include, path
from . import views

urlpatterns = [
    path('storypost/', views.StoryPost.as_view(), name="post_story"),
    path('flagged_stories/', views.flagged_stories)
]