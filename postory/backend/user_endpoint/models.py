from django.db import models

from post_endpoint.models import Post, Comment, Image
from django.contrib.auth.models import AbstractBaseUser,PermissionManager,BaseUserManager, PermissionsMixin

class UserAccountManager(BaseUserManager):
    def create_user(self, email, password=None, **extra_fields):
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
    followedUsers = models.ManyToManyField("self", related_name="followed", blank=True, symmetrical=False)
    followerUsers = models.ManyToManyField("self", related_name="follower", blank=True, symmetrical=False)
    posts = models.ManyToManyField(Post, related_name="posts", blank=True)
    savedPosts = models.ManyToManyField(Post, related_name="savedPosts", blank=True)
    likedPosts = models.ManyToManyField(Post, related_name="likedPosts", blank=True)
    comments = models.ManyToManyField(Comment, blank=True)
    isBanned = models.BooleanField(default=False)
    isAdmin = models.BooleanField(default=False)   
    isPrivate = models.BooleanField(default=False)
    is_active = models.BooleanField(default=False)
    is_staff = models.BooleanField(default=False)
    userPhoto = models.ManyToManyField(Image, blank=True)

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['name', "surname", "username"]
    
    objects = UserAccountManager()

    def __str__(self):
        return str(self.id) + " - " + self.email

class FollowRequest(models.Model):
    fromUser = models.ForeignKey(User, on_delete=models.CASCADE, related_name="requestFrom", blank=True)
    toUser = models.ForeignKey(User, on_delete=models.CASCADE, related_name="requestTo", blank=True)
    
    def __str__(self):
        return str(self.id) + " - " + "From " + self.fromUser.username + " to " + self.toUser.username

class UserReport(models.Model):
    fromUser = models.ForeignKey(User, on_delete=models.CASCADE, related_name="fromUser", blank=True)
    toUser = models.ForeignKey(User, on_delete=models.CASCADE, related_name="toUser", blank=True)
    
    def __str__(self):
        return "User report from " + self.fromUser.username + " to " + self.toUser.username

class StoryReport(models.Model):
    fromStory = models.ForeignKey(User, on_delete=models.CASCADE, related_name="fromStory", blank=True)
    toStory = models.ForeignKey(Post, on_delete=models.CASCADE, related_name="toStory", blank=True)
    
    def __str__(self):
        return "Story report from " + self.fromStory.username + " to " + self.toStory.title