from django.urls import path, include
from . import views


urlpatterns = [
    # path('location/', views.location),
    path('story/', views.StoryList.as_view(), name="list_story"),
    path('storypost/', views.StoryPost.as_view(), name="post_story"),
    path('story/<int:pk>/', views.StoryListDetail.as_view(), name="detail_story"),
    path('location/', views.Locations.as_view(), name="location_story"),
    # path('location/<int:pk>/', views.locationID),
    path('location/<int:pk>/', views.LocationDetail.as_view(), name="location_detail_story"),
    path('location/map/<int:pk>/', views.locationMap, name="location_map_story")
    
]