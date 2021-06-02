from django.urls import path, include
from . import views

urlpatterns = [
    path('nearbyplaces/<int:pk>', views.get_places_near_story_location)
]