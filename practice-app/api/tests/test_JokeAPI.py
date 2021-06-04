from django.http import response
from django.test import TestCase, Client


class JokesTestCase(TestCase):
    def test_get_jokes_for_category(self):
        c = Client()
    
        response = c.get('/api/joke/ani')
        self.assertEqual(response.status_code, 404)

        response2 = c.get('/api/joke/animal')
        self.assertEqual(response2.status_code,200)