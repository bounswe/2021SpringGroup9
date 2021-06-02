from django.urls import path
from . import views

urlpatterns = [
    path('getquote/<int:pid>', views.GetQuote.as_view()),
    path('postquote/<int:pk>', views.FavQuote.as_view()),
]