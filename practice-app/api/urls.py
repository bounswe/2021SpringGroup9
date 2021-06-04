from django.urls import include, path
from .views import view_post_story

urlpatterns = [
    path('storypost/', view_post_story.StoryPost.as_view(), name="post_story"),
    path('flagged_stories/', view_post_story.flagged_stories)
]