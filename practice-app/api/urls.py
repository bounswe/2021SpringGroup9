from django.urls import path

from . import views

urlpatterns = [
    path('city/<int:story_id>', views.get_cityinfo, name="city"),
]

