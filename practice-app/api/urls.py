from django.urls import path, include
from . import views

urlpatterns = [
    path('joke/<str:category>', views.joke,name = "jokes")
] 