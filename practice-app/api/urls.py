from django.urls import path, include
from .views import jokeAPI_view 

urlpatterns = [
    path('joke/<str:category>', jokeAPI_view.joke,name = "jokes")
] 