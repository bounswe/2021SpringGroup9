from django.urls import path
from .views import view_nearbyplaces

urlpatterns = [
    path('nearbyplaces/<int:pk>', view_nearbyplaces.get_places_near_story_location)
]
