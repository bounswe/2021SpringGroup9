from django.db import models

from post_endpoint.models import Post, Comment
from django.contrib.auth.models import AbstractBaseUser,PermissionManager,BaseUserManager, PermissionsMixin

class UserAccountManager(BaseUserManager):
    def create_user(self, email, password=None, **extra_fields):
        if not email:
            raise ValueError('Users must have an username')

        email = self.normalize_email(email)
        user = self.model(username=extra_fields["username"],email=email,name=extra_fields["name"],surname=extra_fields["surname"])

        user.set_password(password)
        user.save()

        return user

class User(AbstractBaseUser, PermissionsMixin):
    username = models.CharField(max_length=200, unique=True)
    name = models.CharField(max_length=200)
    surname = models.CharField(max_length=200)
    email = models.EmailField(max_length=200, unique=True)
    followedUsers = models.ManyToManyField("self")
    followerUsers = models.ManyToManyField("self")
    posts = models.ManyToManyField(Post, related_name="posts")
    savedPosts = models.ManyToManyField(Post, related_name="savedPosts")
    likedPosts = models.ManyToManyField(Post, related_name="likedPosts")
    comments = models.ManyToManyField(Comment)
    isBanned = models.BooleanField(default=False)
    isAdmin = models.BooleanField(default=False)   
    isPrivate = models.BooleanField(default=False)
    is_active = models.BooleanField(default=False)

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['name', "surname", "username"]
    
    objects = UserAccountManager()