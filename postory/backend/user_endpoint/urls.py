from django.urls import include, path
from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from .views import *

urlpatterns = [
    path('follow/<int:pk>/', UserFollowing.as_view(), name="follow_user"),
    path('get/<int:pk>/', UserGet.as_view(), name="get_user"),
]
