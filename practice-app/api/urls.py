from django.urls import path

from .views import view_cityAPI

urlpatterns = [
    path('city/<int:story_id>', view_cityAPI.get_cityinfo, name="city"),
]

