from django.test import TestCase, Client
from django.urls import reverse, resolve

from .views import *
from .models import *

import json
# Create your tests here.


class TestUrls(TestCase):

    def test_list_story_url_resolve(self):
        url = reverse('list_story')
        self.assertEqual(resolve(url).func.view_class, StoryList)

    def test_post_story_url_resolve(self):
        url = reverse('post_story')
        self.assertEqual(resolve(url).func.view_class, StoryPost)
        
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

class TestViews(TestCase):

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

    def test_story_POST(self):
        response = self.client.post(reverse('post_story'), self.mock_data)
        self.assertEqual(response.status_code, 201)
        self.assertEqual(response.data['name'], 'melih')

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

class TestModels(TestCase):

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