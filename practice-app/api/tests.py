from django.test import TestCase,Client
from .models import Story
import requests
from .views import get_cityinfo
from django.urls import reverse


class CityTest(TestCase):
   
    def setUp(self):
        self.client = Client()
        self.story = Story.objects.create(
            title = 'titlex',
    		story = 'storyy',
    		name = 'namez',
    		longitude = 118.387099,
    		latitude = 33.832213,
    		location = 'Istanbul',
    		tag = 'tagw'
        )
        
    def test_works(self):  
        
        resp = self.client.get("/api/city/1")
        self.assertEqual(resp.status_code,200)
    
    def test_not_exist_story(self): 

        resp=self.client.get("/api/city/2")
        self.assertEqual(resp.status_code,404)
    
    def test_string_parameter(self):  
        resp=self.client.get("/api/city/asd")
        self.assertEqual(resp.status_code,404)

    