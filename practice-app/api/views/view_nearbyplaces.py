import requests
from django.http import JsonResponse, HttpResponseNotFound, HttpResponseServerError
from ..models import Story
import environ
from rest_framework.decorators import api_view

env = environ.Env()
environ.Env.read_env('.env')

# Get Google Maps API key
GOOGLE_MAPS_API_KEY = env('GOOGLE_MAPS_API_KEY')

@api_view(['GET'])
def get_places_near_story_location(request, pk):
    """ Returns places near location of the story with given story_id."""

    # Get the story object
    try:
        story = Story.objects.get(id=pk)
    except Story.DoesNotExist:
        return HttpResponseNotFound(f"Story object with story_id {pk} does not exist.")

    # Get list of nearby places using Google Maps API.
    try:
        response = requests.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json",
                                params={
                                    "key": GOOGLE_MAPS_API_KEY,
                                    "location": f"{story.latitude},{story.longitude}",
                                    "radius": "2000",  # Scan places in 2000 meters radius
                                    "language": "en",
                                })
        response.raise_for_status()
    except:
        return HttpResponseServerError("Could not establish a successful connection with Google Maps API.")

    # Construct the json and return it
    try:
        places = [{
            "name": place["name"],
            "location": place["geometry"]["location"],
            "vicinity": place["vicinity"]
        } for place in response.json()["results"]]
        return JsonResponse(places, safe=False)
    except:
        return HttpResponseServerError("Place not found.", status = 404)