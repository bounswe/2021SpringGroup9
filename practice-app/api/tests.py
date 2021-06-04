from django.test import TestCase, SimpleTestCase
from django.urls import reverse, resolve
from django.test.client import Client
from .models import Story
from .views import get_covid_numbers

class TestUrls_COVID(SimpleTestCase):

	def test_urls(self):
		"""
		Tests whether the url is working correctly or not
		"""
		url = reverse('covid_numbers', args=[1])
		self.assertEquals(resolve(url).func, get_covid_numbers)

class TestViews_COVID(TestCase):

	def setUp(self):
		"""
		Sets up the common Client object to use it for GET responses.
		Also creates the URLs and the Story objects to use later to get responses and also
		to make comparisons.
		"""
		self.client = Client()
		self.url_valid = reverse('covid_numbers', args=[1])
		self.url_not_exists = reverse('covid_numbers', args=[5])
		self.url_invalid = reverse('covid_numbers', args=[2])
		self.post_valid = Story.objects.create(
			id = 1,
			title = 'TestTitle',
    		story = 'TestStory',
    		name = 'TestName',
    		longitude = 1.0,
    		latitude = 1.0,
    		location = 'Turkey',
    		tag = 'TestTag'
			)
		self.post_invalid = Story.objects.create(
			id = 2,
			title = 'TestTitle',
    		story = 'TestStory',
    		name = 'TestName',
    		longitude = 1.0,
    		latitude = 1.0,
    		location = 'Country Not Exists',
    		tag = 'TestTag'
			)

	def test_get_covid_numbers_success(self):
		"""
		Working test case, URL is as it should be
		"""
		response = self.client.get(self.url_valid)
		self.assertEquals(response.status_code, 200)

	def test_get_covid_numbers_id_not_exists(self):
		"""
		Test case for the URL maps to non existing Story instance
		"""
		response = self.client.get(self.url_not_exists)
		self.assertEquals(response.status_code, 404)

	def test_get_covid_numbers_response_fails(self):
		"""
		Requests with an invalid country to Covid-19 API
		"""
		response = self.client.get(self.url_invalid)
		self.assertEquals(response.status_code, 500)

from django.test import TestCase,Client
from .models import *
import requests
from .views import *
from django.urls import reverse, resolve
import json

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


class TestUrls_LOCATION(TestCase):

    def test_list_story_url_resolve(self):
        url = reverse('list_story')
        self.assertEqual(resolve(url).func.view_class, StoryList)
        
    def test_detail_story_url_resolve(self):
        url = reverse('detail_story', args=[1])
        self.assertEqual(resolve(url).func.view_class, StoryListDetail)
    
    def test_location_story_url_resolve(self):
        url = reverse('location_story')
        self.assertEqual(resolve(url).func.view_class, Locations)
    
    def test_location_detail_story_url_resolve(self):
        url = reverse('location_detail_story', args=[1])
        self.assertEqual(resolve(url).func.view_class, LocationDetail)
    
    def test_location_map_story_url_resolve(self):
        url = reverse('location_map_story', args=[1])
        self.assertEqual(resolve(url).func, locationMap)

class TestViews_101(TestCase):

    def setUp(self):
        self.client = Client()
        self.mock_data = {"name":"melih",
                          "location":"Karaköy",
                          "tag":"Chilling",
                          "title":"Kahve",
                          "story":"Kahve Keyfi"}
        self.mock_data_1 = Story.objects.create(title = "Çay",
                               story="Çay keyfi",
                               name ="emre",
                               longitude=10,
                               latitude=20,
                               location="Beşiktaş",
                               tag="mutlu")

    def test_story_list_GET(self):
        response = self.client.get(reverse('list_story'))
        self.assertEqual(response.status_code, 200)
        self.assertEqual(json.loads(response.content)[0]['name'], 'emre')

    def test_story_GET_detail_1(self):
        response = self.client.get(reverse('detail_story', args = [2]))
        self.assertEqual(response.status_code, 404)

    def test_story_GET_detail_2(self):
        response = self.client.get(reverse('detail_story', args = [1]))
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.data['name'], 'emre')

    def test_story_PUT_detail(self):
        data = self.client.get(reverse('detail_story', args = [1]))
        data.data['name'] = 'kaan'
        response = self.client.put(reverse('detail_story', args = [1]), data.data, content_type='application/json')
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.data['name'], 'kaan')

    def test_story_DELETE_detail(self):
        data = self.client.get(reverse('detail_story', args = [1]))
        response = self.client.delete(reverse('detail_story', args = [1]), data.data, content_type='application/json')
        self.assertEqual(response.status_code, 204)

    def test_story_GET_location_list(self):
        response = self.client.get(reverse('location_story'))
        self.assertEqual(response.status_code, 200)
        self.assertEqual(json.loads(response.content)[0]['story_id'], 1)

    def test_story_GET_location_detail_1(self):
        response = self.client.get(reverse('location_detail_story', args = [2]))
        self.assertEqual(response.status_code, 404)

    def test_story_GET_location_detail_2(self):
        response = self.client.get(reverse('location_detail_story', args = [1]))
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.data['story_id'], 1)

    def test_story_GET_location_map_1(self):
        response = self.client.get(reverse('location_map_story', args = [2]))
        self.assertEqual(response.status_code, 404)

    def test_story_GET_location_map_2(self):
        response = self.client.get(reverse('location_map_story', args = [1]))
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.headers['Content-type'], 'image/png')

class TestModels_LOCATION(TestCase):

    def setUp(self):
        self.mock_data = Story.objects.create(title = "Çay",
                               story="Çay keyfi",
                               name ="emre",
                               longitude=10,
                               latitude=20,
                               location="Beşiktaş",
                               tag="mutlu")

    def test_story_model(self):
        self.assertNotEqual(self.mock_data.id, None)
        self.assertEqual(self.mock_data.id, 1)
