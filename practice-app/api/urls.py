from django.urls import path

from .views import views_quote
from .views import views_covidAPI as views

urlpatterns = [
    path('quote/<int:pk>', views_quote.GetQuoteTag.as_view(), name="get_quote_tag"),
    path('postquote/<int:pk>', views_quote.FavQuote.as_view(), name="fav_quote"),
    path('quote/location/<int:pk>', views_quote.GetQuoteLoc.as_view(), name="get_quote_loc"),
    path('posts/covid/<int:story_id>', views.get_covid_numbers, name='covid_numbers'),
]


