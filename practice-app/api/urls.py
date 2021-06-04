from django.urls import include, path
from .views import view_post_story
from .views import view_translationAPI_niyazi
from .views import view_locationAPI
from .views import jokeAPI_view 
from .views import view_nearbyplaces
from .views import view_cityAPI
from .views import views_weatherAPI_mertlkn
from .views import views_quote
from .views import views_covidAPI as views

urlpatterns = [
    path('storypost/', view_post_story.StoryPost.as_view(), name="post_story"),
    path('flagged_stories/', view_post_story.flagged_stories),
    path('story/<int:pk>/translate_<str:target>/', view_translationAPI_niyazi.TranslationView.as_view()),
    path('api-auth/', include('rest_framework.urls', namespace='rest_framework')),
    path('story/', view_locationAPI.StoryList.as_view(), name="list_story"),
    path('story/<int:pk>/', view_locationAPI.StoryListDetail.as_view(), name="detail_story"),
    path('location/', view_locationAPI.Locations.as_view(), name="location_story"),
    path('location/<int:pk>/', view_locationAPI.LocationDetail.as_view(), name="location_detail_story"),
    path('location/map/<int:pk>/', view_locationAPI.locationMap, name="location_map_story"),
    path('joke/<str:category>', jokeAPI_view.joke,name = "jokes"),
    path('nearbyplaces/<int:pk>', view_nearbyplaces.get_places_near_story_location),
    path('city/<int:story_id>', view_cityAPI.get_cityinfo, name="city"),
    path('weather/<int:story_id>', views_weatherAPI_mertlkn.weather, name="weather"),
    path('weather/<slug:story_id>', views_weatherAPI_mertlkn.weather, name="weatherSlug"),
    path('quote/<int:pk>', views_quote.GetQuoteTag.as_view(), name="get_quote_tag"),
    path('postquote/<int:pk>', views_quote.FavQuote.as_view(), name="fav_quote"),
    path('quote/location/<int:pk>', views_quote.GetQuoteLoc.as_view(), name="get_quote_loc"),
    path('posts/covid/<int:story_id>', views.get_covid_numbers, name='covid_numbers'),

]

