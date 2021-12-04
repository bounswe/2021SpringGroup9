from django.urls import include, path
from drf_yasg.views import get_schema_view
from drf_yasg import openapi
from .views import *

urlpatterns = [
    path('create', PostCreate.as_view(), name="create_post"),
    path('get/<int:pk>', PostListDetail.as_view(), name="get_post"),
    path('put/<int:pk>', PostUpdate.as_view(), name="update_post"),
    path('delete/<int:pk>', PostDelete.as_view(), name="delete_post"),
    path('all', GetFollowedUsersPosts.as_view(), name="get_all_posts_followed_users"),
    path('all/admin', GetAllPosts.as_view(), name="get_all_posts_admin"),
    path('all/user/<str:username>', GetUsersPosts.as_view(), name="get_all_posts_of_user"),
]
