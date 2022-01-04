from django.urls import include, path
from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from .views import *

urlpatterns = [
    path('search/<slug:term>', SearchUser.as_view(), name="search_user"),
    path('addPhoto', AddPhoto.as_view(), name="add_photo"),
    path('follow/<int:pk>', UserFollowing.as_view(), name="follow_user"),
    path('get/<int:pk>', UserGet.as_view(), name="get_user"),
]
