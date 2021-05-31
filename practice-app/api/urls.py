from django.urls import path, include
from rest_framework import routers
from rest_framework.urlpatterns import format_suffix_patterns
from . import views


urlpatterns = [
    path('location/<int:pk>/', views.location),
    path('story/', views.StoryList.as_view()),
    path('story/<int:pk>/', views.StoryListDetail.as_view()),
    path('api-auth/', include('rest_framework.urls', namespace='rest_framework'))
]
urlpatterns = format_suffix_patterns(urlpatterns)