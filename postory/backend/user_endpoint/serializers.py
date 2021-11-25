from rest_framework import serializers

from .models import *



class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'username', 'password', 'name', 'surname', 'email', 'followedUsers', 'followerUsers', 'posts', 'savedPosts', 'likedPosts', 'comments', 'isBanned', 'isAdmin']
        read_only_fields = []


