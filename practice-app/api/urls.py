from django.urls import path
from . import views

urlpatterns = [
    path('getquote/<int:pid>', views.GetQuote.as_view(), name="get_quote"),
    path('postquote/<int:pk>', views.FavQuote.as_view(), name="fav_quote"),
]