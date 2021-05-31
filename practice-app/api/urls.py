from django.urls import include, path
from . import views

urlpatterns = [
    path('post_story/', views.post_story),
]