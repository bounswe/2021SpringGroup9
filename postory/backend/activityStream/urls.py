from django.urls import include, path
from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from .views import *

urlpatterns = [
    path('own', GetOwnActivities.as_view(), name="get_own_activities"),
    path('followed', GetFollowedActivities.as_view(), name="get_followed_activities"),
    path('all', GetAllActivities.as_view(), name="get_all_activities"),
]
