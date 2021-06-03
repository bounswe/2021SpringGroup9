from django.test import TestCase, Client


class GettingPlacesTestCase(TestCase):
    def test_get_places(self):
        c = Client()
        response1 = c.get('/api/nearbyplaces/123123')
        self.assertEqual(response1.status_code, 404)
        
        response2 = c.get('/api/nearbyplaces/1')
        self.assertNotIn(response2.status_code, (404, 500))
        self.assertNotEqual(response2.json(), [])

        response3 = c.get('/api/nearbyplaces/asdfgh')
        self.assertEqual(response1.status_code, 404)
