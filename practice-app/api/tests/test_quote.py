from django.test import TestCase, Client
from django.urls import reverse

from ..views import views_quote
from ..models import Quote, Story


class TestViews(TestCase):
    """
    Set up the test of the views by creating a Quote and Story object.
    """
    def setUp(self):
        self.client = Client()
        self.story1 = Story.objects.create(
                id = 1, 
                title = "A Wonderful Day in Istanbul", 
                story = "It was my first time visiting Istanbul. It is full of places to see and enjoy. Can't wait to visit Istanbul again!",
                name = "Leyla", 
                longitude = 41,
                latitude = 28,
                location = "Istanbul",
                tag = "Joy"
        )
        
        self.story2 = Story.objects.create(
                id = 2,
                title = "A Wonderful Day in Istanbul", 
                story = "It was my first time visiting Istanbul. It is full of places to see and enjoy. Can't wait to visit Istanbul again!",
                name = "Leyla", 
                longitude = 41,
                latitude = 28,
                location = "",
                tag = "Joy"
        )

        self.quote = Quote.objects.create(
                id = 700,
                author = "John Keats",
                body = "A thing of beauty is a joy forever: its loveliness increases it will never pass into nothingness.",
                likes = 0
        )

    """
    Test whether a request according to tag doesn't give error. 
    """
    def test_get_quote_tag(self):
        response = self.client.get(reverse('get_quote_tag', args=(self.story1.id,)))
        self.assertNotEqual(response.status_code, 400)
    
    """
    Test whether a request according to tag is returned succesfully. 
    """
    def test_get_quote_tag_success(self):
        response = self.client.get(reverse('get_quote_tag', args=(self.story1.id,)))
        self.assertEqual(response.status_code, 200)

    """
    Test whether a request according to location doesn't give error. 
    """
    def test_get_quote_loc(self):
        response = self.client.get(reverse('get_quote_loc', args=(self.story1.id,)))
        self.assertNotEqual(response.status_code, 400)
    
    """
    Test whether a request according to location is returned successfully. 
    """
    def test_get_quote_loc_success(self):
        response = self.client.get(reverse('get_quote_loc', args=(self.story1.id,)))
        self.assertEqual(response.status_code, 200)

    """
    Test whether a quote according to location exists. 
    """
    def test_quote_loc_exist(self):
        response = self.client.get(reverse('get_quote_loc', args=(self.story1.id,)))
        self.assertIsNotNone(response.data['Quote'])

    """
    Test whether the quote post request doesn't raise errors. 
    """
    def test_quote_response(self):
        response = self.client.post(reverse('fav_quote', args=(self.quote.id,)))
        self.assertNotEqual(response.status_code, 400)

    """
    Test whether the quote post request returns successfully. 
    """
    def test_post_success(self): 
        response = self.client.post(reverse('fav_quote', args=(self.quote.id,)))
        self.assertEqual(response.status_code, 200)

    """
    Test whether the posted quote's likes is increased by 1 or not.
    """
    def test_post_like(self):
        likes = self.quote.likes
        response = self.client.post(reverse('fav_quote', args=(self.quote.id,)))
        self.assertEquals(likes+1, response.data['likes'])