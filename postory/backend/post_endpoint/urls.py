from django.urls import include, path
from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from .views import *

urlpatterns = [
    path('create', PostCreate.as_view(), name="create_post"),
    path('get/<int:pk>', GetFollowedUsersPosts.as_view(), name="get_post_followed"),
    path('get/user/<int:pk>', GetUsersPosts.as_view(), name="get_post_user"),
    path('get/admin/<int:pk>', PostListDetail.as_view(), name="get_post_admin"),
    path('put/<int:pk>', PostUpdate.as_view(), name="update_post"),
    path('delete/<int:pk>', PostDelete.as_view(), name="delete_post"),
    path('all', GetAllPosts.as_view(), name="get_all_posts"),
]
