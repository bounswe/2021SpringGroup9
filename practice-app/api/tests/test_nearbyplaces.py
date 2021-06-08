from django.test import TestCase, Client
from ..models import Story


class GettingPlacesTestCase(TestCase):
    def setUp(self):
        Story.objects.create(id = 1,
            title="My Story",
            story="Story body",
            name="John",
            latitude=37.184,
            longitude=-3,
            location="Home",
            tag="Travel",
            notifyAdmin=False
        )

        Story.objects.create(id = 2,
            title="Your Story",
            story="Your Story body",
            name="Johanna",
            latitude=37.184,
            longitude=-3,
            location="Home",
            tag="Travel",
            notifyAdmin=False
        )

    
    def test_existing_place(self):
        """ Tests getting nearby places of a story which exists in the database """
        c = Client()
        response = c.get('/api/nearbyplaces/1')
        self.assertNotIn(response.status_code, (404, 500))
        self.assertNotEqual(response.json(), [])

    
    def test_non_existing_place(self):
        """ Tests getting nearby places of a story which does not in the database
        This should return 404 """
        c = Client()
        response = c.get('/api/nearbyplaces/123123')
        self.assertEqual(response.status_code, 404)


    def test_incorrect_api_request(self):
        """ Tests calling the API method with an incorrect parameter.
        This should return 404 """
        c = Client()
        response = c.get('/api/nearbyplaces/asdfgh')
        self.assertEqual(response.status_code, 404)