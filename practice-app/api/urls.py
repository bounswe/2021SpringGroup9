from django.urls import path
from . import views

urlpatterns = [
    path('posts/covid/<int:story_id>', views.get_covid_numbers, name='covid_numbers'),
]