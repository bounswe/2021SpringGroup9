from djoser.serializers import UserCreateSerializer
from django.contrib.auth import get_user_model
from rest_framework import serializers

from .models import User as U
from .models import FollowRequest as F
from .models import UserReport as UR
from.models import StoryReport as SR

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

class FollowRequestSerializer(serializers.ModelSerializer):
    class Meta:
        model = F
        fields = ['id', 'fromUser', 'toUser']
        read_only_fields = []

class UserReportSerializer(serializers.ModelSerializer):
    class Meta:
        model = UR
        fields = ['id', 'fromUser', 'toUser']
        read_only_fields = []

class StoryReportSerializer(serializers.ModelSerializer):
    class Meta:
        model = SR
        fields = ['id', 'fromStory', 'toStory']
        read_only_fields = []


