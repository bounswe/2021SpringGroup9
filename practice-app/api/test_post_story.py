from django.test import TestCase
from .models import Post
from .post_story import *
from unittest import skip


class PostStoryTestCase(TestCase):

    def setUp(self):
        
        Post.objects.create(title = "Testing Story", story= "Once upon a time ...", location_long = 
        3.14, location_lat = 4.13, notify_admin = False)
        Post.objects.create(title = "Testing Story2", story= "This notifies the admin.", location_long = 
        3.14, location_lat = 4.13, notify_admin = True)

    def test_can_find_notify_admin(self):
        self.assertEqual(Post.objects.get(notify_admin = True).story, "This notifies the admin.")

    def test_creates_post(self): 
        dummy_body = json.dumps({'title':'TEST', 'story': 'STILL TESTING.', 'long':0, 'lat': 0})
        response = self.client.post('/api/post_story/', data = dummy_body, content_type="application/json")
        self.assertEqual(response.status_code, 200)
    
    def test_creates_flagged_post(self):
        dummy_body = json.dumps({'title':'TEST', 'story': 'Just shut up!', 'long':0, 'lat': 0})
        response = self.client.post('/api/post_story/', data = dummy_body, content_type="application/json")
        self.assertEqual(response.status_code, 200)
        notify_flag = Post.objects.get(id = response.json()['id']).notify_admin
        self.assertEqual(notify_flag, True)
