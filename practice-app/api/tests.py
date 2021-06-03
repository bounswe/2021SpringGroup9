from django.test import TestCase,Client
from .models import Story
import requests
from .views import weather
# Create your tests here.

class WeatherTest(TestCase):
    def setUp(self):
        Story.objects.create(
            title = "Title1",
            story = "Story1",
            name = "User1",
            longitude = 41,
            latitude = 28,
            location = "Istanbul",
            tag = "Tag1",
            notifyAdmin = False
        )
        Story.objects.create(
            title = "Title2",
            story = "Story2",
            name = "User2",
            longitude = 4100,
            latitude = 28,
            location = "Not Istanbul",
            tag = "Tag2",
            notifyAdmin = True
        )

    def test_post_requests(self):
        c=Client()
        resp=c.post("/api/weather/1")
        self.assertEqual(resp.status_code,400)
    
    def test_put_requests(self):
        c=Client()
        resp=c.put("/api/weather/1")
        self.assertEqual(resp.status_code,400)
    
    def test_delete_requests(self):
        c=Client()
        resp=c.delete("/api/weather/1")
        self.assertEqual(resp.status_code,400)

    def test_when_story_does_not_exist(self):
        c=Client()
        resp=c.get("/api/weather/3")
        self.assertEqual(resp.status_code,404)
    
    def test_when_openweather_api_fails(self):
        c=Client()
        resp=c.get("/api/weather/2")
        self.assertEqual(resp.status_code,400)
    
    def test_works_correct(self):  
        c=Client()  
        resp=c.get("/api/weather/1")
        self.assertEqual(resp.status_code,200)

    def test_string_type_parameter(self):  
        c=Client()  
        resp=c.get("/api/weather/mert")
        self.assertEqual(resp.status_code,400)
    