from django.urls import path, include
from .views import view_locationAPI


urlpatterns = [
    path('story/', view_locationAPI.StoryList.as_view(), name="list_story"),
    path('story/<int:pk>/', view_locationAPI.StoryListDetail.as_view(), name="detail_story"),
    path('location/', view_locationAPI.Locations.as_view(), name="location_story"),
    path('location/<int:pk>/', view_locationAPI.LocationDetail.as_view(), name="location_detail_story"),
    path('location/map/<int:pk>/', view_locationAPI.locationMap, name="location_map_story")
]