from django.db import models

from postory.backend.post_endpoint.models import Post, Comment


class User(models.Model):
    username = models.CharField(max_length=200)
    password = models.CharField(max_length=500)
    name = models.CharField(max_length=200)
    surname = models.CharField(max_length=200)
    email = models.EmailField(max_length=200)
    followedUsers = models.ManyToManyField("self")
    followerUsers = models.ManyToManyField("self")
    posts = models.ManyToManyField(Post)
    savedPosts = models.ManyToManyField(Post)
    likedPosts = models.ManyToManyField(Post)
    comments = models.ManyToManyField(Comment)
    isBanned = models.BooleanField(default=False)
    isAdmin = models.BooleanField(default=False)   