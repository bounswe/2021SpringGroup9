from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from django.shortcuts import render
# Create your views here.

@csrf_exempt

def homepage(request):
    return render(request,'homepage.html')


def view_story(request,story_id):
    return render(request,'story.html',{'story_id':story_id})

