from django.shortcuts import render
from django.http import HttpResponse
from django.http import HttpResponseServerError
from django.http import HttpResponseNotFound
from django.http import JsonResponse
import json
from .models import Post
import requests

def get_covid_numbers(request, post_id):
    """Returns the new covid cases of the country from which the post is posted"""

    # Get the post object

    try:
        post = Post.objects.get(pk=post_id)
    except Post.DoesNotExist:
        return HttpResponseNotFound(f"Post object with post_id: {post_id} does not exist!")

    # Get the covid cases from Covid Api

    try:
        url = "https://covid-193.p.rapidapi.com/statistics"

        querystring = {"country":post.country}

        headers = {
            'x-rapidapi-key': "a56619ce73mshdeb6d094975d298p119a08jsn43e51763a65a",
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


