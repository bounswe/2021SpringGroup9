from django.test import TestCase,Client
from ..views.view_translationAPI_niyazi import * 


class CheckStoryLengthTest(TestCase):

    def test_short_title(self):
        title = "hey"
        body = "It was a sunny day."
        self.assertFalse(check_story_length(title,body))
    
    def test_short_body(self):
        title = "We went to the cinema."
        body = "beautiful"
        self.assertFalse(check_story_length(title,body))

    def test_false_title_type(self):
        title = 1957
        body = "What a nice year!"
        self.assertRaises(TypeError, check_story_length, title, body)
        
    def test_false_body_type(self):
        title = "We went to the cinema."
        body = 23.948429
        self.assertRaises(TypeError, check_story_length, title, body)
    
    def test_correct_input(self):
        title = "We went to the cinema."
        body = "It was a sunny day."
        self.assertTrue(check_story_length(title,body))

class TranslationTest(TestCase):
    def setUp(self):
        Story.objects.create(
            title = "Title",
            story = "Story",
            name = "User1",
            longitude = 41,
            latitude = 28,
            location = "Istanbul",
            tag = "Tag1",
            notifyAdmin = False
        )
        Story.objects.create(
            title = "Hello World!",
            story = "I am very happy today!",
            name = "User2",
            longitude = 4100,
            latitude = 28,
            location = "Ankara",
            tag = "Tag2",
            notifyAdmin = True
        )

    def test_only_get_requests(self):
        c=Client()
        resp_1=c.post("/api/story/2/translate_es/")
        resp_2=c.put("/api/story/2/translate_es/")
        resp_3=c.delete("/api/story/2/translate_es/")
        self.assertEqual(resp_1.status_code,405)
        self.assertEqual(resp_2.status_code,405)
        self.assertEqual(resp_3.status_code,405)

    def test_when_story_does_not_exist(self):
        c=Client()
        resp=c.get("/api/story/3/translate_ne/")
        self.assertEqual(resp.status_code,404)
    
    def test_when_translator_api_fails(self):
        c=Client()
        resp=c.get("/api/story/2/translate_nosuchlanguage/")
        self.assertEqual(resp.status_code,404)

    def test_short_story(self):
        c=Client()
        resp=c.get("/api/story/1/translate_jp/")
        self.assertEqual(resp.status_code,400)
        
    def test_works_correct(self):  
        c=Client()  
        resp=c.get("/api/story/2/translate_tr/")
        self.assertEqual(resp.status_code,200)