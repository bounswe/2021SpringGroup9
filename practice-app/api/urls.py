from django.urls import path
from .views import view_nearbyplaces
from .views import view_cityAPI
from .views import views_weatherAPI_mertlkn
from .views import views_quote
from .views import views_covidAPI as views

urlpatterns = [
    path('nearbyplaces/<int:pk>', view_nearbyplaces.get_places_near_story_location),
    path('city/<int:story_id>', view_cityAPI.get_cityinfo, name="city"),
    path('weather/<int:story_id>', views_weatherAPI_mertlkn.weather, name="weather"),
    path('weather/<slug:story_id>', views_weatherAPI_mertlkn.weather, name="weatherSlug"),
    path('quote/<int:pk>', views_quote.GetQuoteTag.as_view(), name="get_quote_tag"),
    path('postquote/<int:pk>', views_quote.FavQuote.as_view(), name="fav_quote"),
    path('quote/location/<int:pk>', views_quote.GetQuoteLoc.as_view(), name="get_quote_loc"),
    path('posts/covid/<int:story_id>', views.get_covid_numbers, name='covid_numbers'),

]


