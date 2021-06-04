from django.test import TestCase,Client
from .models import Story
import requests
from .views import weather
# Create your tests here.

class WeatherTest(TestCase):
    def setUp(self):
        """
        An instance that works
        """
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
        """
        An instance with wrong longitude.
        """
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

    """
    Send post request. Should not be allowed.
    """
    def test_post_requests(self):
        c=Client()
        resp=c.post("/api/weather/1")
        self.assertEqual(resp.status_code,405)
    
    """
    Send put request. Should not be allowed.
    """
    def test_put_requests(self):
        c=Client()
        resp=c.put("/api/weather/1")
        self.assertEqual(resp.status_code,405)
    
    """
    Send delete request. Should not be allowed.
    """
    def test_delete_requests(self):
        c=Client()
        resp=c.delete("/api/weather/1")
        self.assertEqual(resp.status_code,405)

    """
    Test when an instance does not exists.
    """
    def test_when_story_does_not_exist(self):
        c=Client()
        resp=c.get("/api/weather/3")
        self.assertEqual(resp.status_code,404)
    
    """
    OpenWeather API fails with wrong longitude or latitude.
    """
    def test_when_openweather_api_fails(self):
        c=Client()
        resp=c.get("/api/weather/2")
        self.assertEqual(resp.status_code,400)
    
    """
    This is an instance that works.
    """
    def test_works_correct(self):  
        c=Client()  
        resp=c.get("/api/weather/1")
        self.assertEqual(resp.status_code,200)

    """
    When parameter is not an integer type.
    """
    def test_string_type_parameter(self):  
        c=Client()  
        resp=c.get("/api/weather/mert")
        self.assertEqual(resp.status_code,400)
    