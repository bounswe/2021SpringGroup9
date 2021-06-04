from django.urls import path, include
from .views import view_translationAPI_niyazi
from rest_framework.urlpatterns import format_suffix_patterns
from rest_framework import routers
urlpatterns = [
    path('story/<int:pk>/translate_<str:target>/', view_translationAPI_niyazi.TranslationView.as_view()),
    path('api-auth/', include('rest_framework.urls', namespace='rest_framework'))
]
urlpatterns = format_suffix_patterns(urlpatterns)