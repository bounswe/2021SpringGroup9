from django.db import models

from post_endpoint.models import Post, Comment


class User(models.Model):
    username = models.CharField(max_length=200)
    password = models.CharField(max_length=500)
    name = models.CharField(max_length=200)
    surname = models.CharField(max_length=200)
    email = models.EmailField(max_length=200)
    followedUsers = models.ManyToManyField("self")
    followerUsers = models.ManyToManyField("self")
    posts = models.ManyToManyField(Post, related_name="posts")
    savedPosts = models.ManyToManyField(Post, related_name="savedPosts")
    likedPosts = models.ManyToManyField(Post, related_name="likedPosts")
    comments = models.ManyToManyField(Comment)
    isBanned = models.BooleanField(default=False)
    isAdmin = models.BooleanField(default=False)   