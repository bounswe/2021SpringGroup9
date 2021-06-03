from django.test import TestCase, SimpleTestCase
from django.urls import reverse, resolve
from django.test.client import Client
from .models import Story
from .views import get_covid_numbers

class TestUrls(SimpleTestCase):

	def test_urls(self):
		url = reverse('covid_numbers', args=[1])
		self.assertEquals(resolve(url).func, get_covid_numbers)

class TestViews(TestCase):

	def setUp(self):
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
		response = self.client.get(self.url_valid)
		self.assertEquals(response.status_code, 200)

	def test_get_covid_numbers_id_not_exists(self):
		response = self.client.get(self.url_not_exists)
		self.assertEquals(response.status_code, 404)

	def test_get_covid_numbers_response_fails(self):
		"""
		Requests with an invalid country to Covid-19 API
		"""
		response = self.client.get(self.url_invalid)
		self.assertEquals(response.status_code, 500)