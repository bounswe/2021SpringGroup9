from django.urls import path

from . import views

urlpatterns = [
    path('posts/covid/<int:story_id>', views.get_covid_numbers, name='covid_numbers'),
    path('city/<int:story_id>', views.get_cityinfo, name="city"),
    path('story/', views.StoryList.as_view(), name="list_story"),
    path('story/<int:pk>/', views.StoryListDetail.as_view(), name="detail_story"),
    path('location/', views.Locations.as_view(), name="location_story"),
    path('location/<int:pk>/', views.LocationDetail.as_view(), name="location_detail_story"),
    path('location/map/<int:pk>/', views.locationMap, name="location_map_story")
    
]

