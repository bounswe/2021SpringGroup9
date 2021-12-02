from djoser.serializers import UserCreateSerializer
from django.contrib.auth import get_user_model
from rest_framework import serializers

from .models import User as U

User = get_user_model()


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = U
        fields = ['id', 'username', 'password', 'name', 'surname', 'email', 'followedUsers', 'followerUsers', 'posts', 'savedPosts', 'likedPosts', 'comments', 'isBanned', 'isAdmin', 'isPrivate', 'is_active']
        read_only_fields = []
        
class UserCreateSerializer(UserCreateSerializer):
    class Meta(UserCreateSerializer.Meta):
        model = User
        fields = ('id', 'username', 'password', 'name', 'surname', 'email')



