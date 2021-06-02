from django.http import response
from django.test import TestCase, Client


class GettingPlacesTestCase(TestCase):
    def test_get_places(self):
        c = Client()
    
        response = c.get('/api/joke/ani')
        self.assertEqual(response.status_code, 200)

        response2 = c.get('/api/joke/animal')
        self.assertEqual(response2.status_code,200)