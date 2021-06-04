from django.urls import path
from .views import views_covidAPI as views

urlpatterns = [
    path('posts/covid/<int:story_id>', views.get_covid_numbers, name='covid_numbers'),
]