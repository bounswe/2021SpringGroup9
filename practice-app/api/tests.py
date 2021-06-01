from django.test import TestCase, SimpleTestCase
from django.urls import reverse, resolve
from django.test.client import Client
from .models import Post
from .views import get_covid_numbers
import json

class TestUrls(SimpleTestCase):

	def test_urls(self):

		url = reverse('covid_numbers', args=[1])

		self.assertEquals(resolve(url).func, get_covid_numbers)

class TestViews(TestCase):

	def setUp(self):
		self.client = Client()
		self.url = reverse('covid_numbers', args=[1])
		self.post1 = Post.objects.create(
			post_id = 1,
			country = 'Australia')

	def test_get_covid_numbers(self):

		response = self.client.get(self.url)

		self.assertEquals(response.status_code, 200)


