from django.http import HttpResponseServerError
from django.http import HttpResponseNotFound
from django.http import JsonResponse
import json
import environ
from .models import Story
import requests

def get_covid_numbers(request, post_id):
    """Returns the new covid cases of the country from which the post is posted"""

    # Get the post object

    env = environ.Env(DEBUG=(bool, False))
    environ.Env.read_env()
    COVID_API_KEY = env('COVID_API_KEY')

    try:
        post = Story.objects.get(pk=post_id)
    except Story.DoesNotExist:
        return HttpResponseNotFound(f"Post object with post_id: {post_id} does not exist!")

    # Get the covid cases from Covid Api

    try:
        url = "https://covid-193.p.rapidapi.com/statistics"

        querystring = {"country":post.location}

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


