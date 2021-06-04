from django.urls import path, include
from .views import views_weatherAPI_mertlkn

urlpatterns = [
    path('weather/<int:story_id>', views_weatherAPI_mertlkn.weather, name="weather"),
    path('weather/<slug:story_id>', views_weatherAPI_mertlkn.weather, name="weatherSlug")
]