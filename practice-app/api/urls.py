from django.urls import include, path
from . import views

urlpatterns = [
    path('weather/<int:post_id>', views.weather),
]