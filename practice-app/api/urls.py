from django.urls import path
from . import views

urlpatterns = [
    path('quote/<int:pk>', views.GetQuoteTag.as_view(), name="get_quote_tag"),
    path('postquote/<int:pk>', views.FavQuote.as_view(), name="fav_quote"),
    path('quote/location/<int:pk>', views.GetQuoteLoc.as_view(), name="get_quote_loc"),
]