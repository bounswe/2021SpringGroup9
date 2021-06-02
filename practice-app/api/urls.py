from django.urls import path, include
from . import views

urlpatterns = [
    path('weather/<int:story_id>', views.weather, name="weather"),
]