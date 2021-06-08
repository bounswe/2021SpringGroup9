from django.http import HttpResponseServerError
from django.http import HttpResponseNotFound
from django.http import JsonResponse
import json
import environ
from ..models import Story
import requests
from rest_framework.decorators import api_view

@api_view(['GET'])
def get_covid_numbers(request, story_id):
    """
    Takes a story_id as a parameter and makes a call to Covid API to get 
    the covid metrics for the location of the Story instance with given story_id.
    Returns the country, current day, new Covid cases, new Covid deaths and total
    active cases for that country in JSON format.
    """

    # Get the story object

    env = environ.Env()
    environ.Env.read_env('.env')
    COVID_API_KEY = env('COVID_API_KEY')

    try:
        story = Story.objects.get(pk=story_id)
    except Story.DoesNotExist:
        return HttpResponseNotFound(f"Story object with story_id: {story_id} does not exist!")

    # Get the covid cases from Covid Api

    try:
        url = "https://covid-193.p.rapidapi.com/statistics"

        querystring = {"country":story.location}

        headers = {
            'x-rapidapi-key': COVID_API_KEY,
            'x-rapidapi-host': "covid-193.p.rapidapi.com"
            }

        response = requests.request("GET", url, headers=headers, params=querystring)

        json_data = json.loads(response.text)
    except:
        return HttpResponseServerError("Could not establish connection with Covid API")

    try:
        final_data = {
            "country": json_data["parameters"]["country"],
            "day": json_data["response"][0]["day"],
            "new cases": json_data["response"][0]["cases"]["new"],
            "new deaths": json_data["response"][0]["deaths"]["new"],
            "active cases": json_data["response"][0]["cases"]["active"],
        }
        return JsonResponse(final_data)
    except:
        return HttpResponseServerError("Error")

