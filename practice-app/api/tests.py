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
		self.url = reverse('covid_numbers', args=[1])
		self.post1 = Story.objects.create(
			id = 1,
			title = 'TestTitle',
    		story = 'TestStory',
    		name = 'TestName',
    		longitude = 1.0,
    		latitude = 1.0,
    		location = 'Turkey',
    		tag = 'TestTag'
			)

	def test_get_covid_numbers(self):

		response = self.client.get(self.url)

		self.assertEquals(response.status_code, 200)


