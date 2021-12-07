from django.urls import path
from .views import *

urlpatterns = [
    path('search/<slug:term>', SearchUser.as_view(), name="search_user"),
    path('addPhoto', AddPhoto.as_view(), name="add_photo")
]
