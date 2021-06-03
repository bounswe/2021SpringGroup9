from django.test import TestCase
from .models import Story
from .post_story import *
from unittest import skip


class PostStoryTestCase(TestCase):

    def setUp(self):
        
        Story.objects.create(title = "Testing Story", story= "Once upon a time ...", name = "USER", longitude = 
        3.14, latitude = 4.13, location = "somewhere", tag =" ", notifyAdmin  = False)
        Story.objects.create(title = "Testing Story2", story= "This notifies the admin.", name = "USER", longitude = 
        3.14, latitude = 4.13, location = "somewhere", tag =" ", notifyAdmin = True)

    def test_can_find_notify_admin(self):
        self.assertEqual(Story.objects.get(notifyAdmin = True).story, "This notifies the admin.")

    def test_creates_post(self): 
        dummy_body = json.dumps({'title':'TEST', 'story': 'STILL TESTING.', 'name':'USER', 
            'long':0, 'lat': 0 ,'location' : 'somwhere'})
        response = self.client.post('/api/story/', data = dummy_body, content_type="application/json")
        self.assertEqual(response.status_code, 200)
    
    def test_creates_flagged_post(self):
        dummy_body = json.dumps({'title':'TEST', 'story': 'Just shut up!', 'name':'USER', 
            'long':0, 'lat': 0 ,'location' : 'somwhere'})
        response = self.client.post('/api/story/', data = dummy_body, content_type="application/json")
        self.assertEqual(response.status_code, 200)
        notify_flag = Story.objects.get(id = response.json()['id']).notifyAdmin
        self.assertEqual(notify_flag, True)
    
    def test_gets_flagged_post(self):
        response = self.client.get('/api/flagged_stories/')
        self.assertEqual(response.status_code, 200)

