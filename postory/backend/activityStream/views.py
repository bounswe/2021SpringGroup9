from django.shortcuts import render
from post_endpoint.serializers import PostSerializer

from user_endpoint.serializers import UserSerializer

from .models import ActivityStream
from post_endpoint.models import Post
from user_endpoint.models import User
from post_endpoint.models import Image

from .serializers import ActivityStreamSerializer
import jwt
import json
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView

def createActivity(actor,summary,object,url,type,success):
    data = dict()
    data['actor'] = actor
    data['summary'] = summary
    data['object'] = object
    data['url'] = url
    data['type'] = type
    data['success'] = success
    serializer = ActivityStreamSerializer(data=data)
    if(serializer.is_valid()):
        serializer.save()
    return

class GetAllActivities(GenericAPIView):

    """
        Returns public users' activities.
    """

    def get(self,request,format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        allUsers = [user.id for user in User.objects.all()]
        activities = ActivityStream.objects.filter(actor__in = allUsers).filter(success__in = [True]).all()
        serializer = dict()
        for activity in activities:
            if(activity.type == "PostCreate" or activity.type == "PostUpdate" or activity.type == "PostComment" or activity.type == "PostLike" or activity.type == "UserAddPhoto" or activity.type == "UserFollow"):
                serializer[activity.id] = get_activity(activity)
            else:
                pass
        return Response(serializer.values(), status=200)    

class GetOwnActivities(GenericAPIView):

    """
        Returns activities of the requester. 
        Requester user id is taken from jwt key.
    """

    def get(self,request,format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        activities = ActivityStream.objects.filter(actor = user_id).filter(success__in = [True]).all()
        serializer = dict()
        for activity in activities:
            if(activity.type == "PostCreate" or activity.type == "PostUpdate" or activity.type == "PostComment" or activity.type == "PostLike" or activity.type == "UserAddPhoto" or activity.type == "UserFollow"):
                serializer[activity.id] = get_activity(activity)
            else:
                pass
        return Response(serializer.values(), status=200)

class GetFollowedActivities(GenericAPIView):

    """
        Returns activites of followed users of requester user.
        Requester user id is taken from jwt key.
    """

    def get(self,request,format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        followed = [user.id for user in User.objects.filter(id = user_id).first().followedUsers.all()]
        activities = ActivityStream.objects.filter(actor__in = followed).filter(success__in = [True]).all()
        serializer = dict()
        for activity in activities:
            if(activity.type == "PostCreate" or activity.type == "PostUpdate" or activity.type == "PostComment" or activity.type == "PostLike" or activity.type == "UserAddPhoto" or activity.type == "UserFollow"):
                serializer[activity.id] = get_activity(activity)
            else:
                pass
        return Response(serializer.values(), status=200)

def get_activity(activity):
    serializer = dict()
    user = User.objects.filter(id = activity.actor).first()
    if(activity.type.startswith("Post")):
        try:
            story = Post.objects.filter(id = activity.object).first()
            object = get_story(story)
        except:
            object = {}
    elif(activity.type == "UserAddPhoto"):
        try:
            image = Image.objects.filter(id = activity.object).first()
            object = image.file.url
        except:
            object = {}
    elif(activity.type == "UserFollow"):
        try:
            affectedUser = User.objects.filter(id = activity.object).first()
            object = get_user(affectedUser)
        except:
            object = {}
    try:
        serializer['actor'] = get_user(user)
    except:
        serializer['actor'] = {}
    serializer['object'] = object
    serializer['type'] = activity.type
    serializer['date'] = activity.date
    return serializer


def get_story(story):
    username = story.username
    user = User.objects.filter(username = username).first()
    try:
        userPhoto = Image.objects.filter(user = user.id).first()
        userPhoto = userPhoto.file.url
    except:
        userPhoto = ""
    tags = []
    for tag in story.tags.all():
        tags.append(tag.content)
    locations = []
    for location in story.locations.all():
        temp = []
        temp.append(location.name)
        temp.append(location.coordsLatitude)
        temp.append(location.coordsLongitude)
        locations.append(temp)
    images = []
    for image in story.images.all():
        images.append(image.file.url)
    comments = []
    for comment in story.comments.all():
        temp = []
        temp.append(comment.userid)
        temp.append(User.objects.get(pk=comment.userid).username)
        temp.append(comment.comment)
        comments.append(temp)
    likeId = story.likeList.split(',')
    if likeId[0] == "":
        likeId = []
    likeId = [int(id) for id in likeId]
    likeList = []
    for id in likeId:
        temp = []
        temp.append(id)
        temp.append(User.objects.get(pk=id).username)
        likeList.append(temp)
    if story.year:
        year = [int(time) for time in story.year.split(',')]
    else:
        year = []
        
    if story.month:
        month = [int(time) for time in story.month.split(',')]
    else:
        month = []
        
    if story.day:
        day = [int(time) for time in story.day.split(',')]
    else:
        day = []
        
    if story.hour:
        hour = [int(time) for time in story.hour.split(',')]
    else:
        hour = []
        
    if story.minute:
        minute = [int(time) for time in story.minute.split(',')]
    else:
        minute = []
    serializer = dict(PostSerializer(story).data)
    serializer['tags'] = tags
    serializer['locations'] = locations
    serializer['images'] = images
    serializer['comments'] = comments
    serializer['likeList'] = likeList
    serializer['year'] = year
    serializer['month'] = month
    serializer['day'] = day
    serializer['hour'] = hour
    serializer['minute'] = minute
    serializer['username'] = username
    serializer['userPhoto'] = userPhoto
    serializer['id'] = story.id
    return serializer

def get_user(user):
    serializer = dict(UserSerializer(user).data)
    followers = []
    for followerUser in user.followerUsers.all():
        temp = {}
        temp['id'] = followerUser.id
        temp['username'] = followerUser.username
        temp['name'] = followerUser.name
        temp['surname'] = followerUser.surname
        temp['email'] = followerUser.email
        temp['isBanned'] = followerUser.isBanned
        temp['isAdmin'] = followerUser.isAdmin
        temp['isPrivate'] = followerUser.isPrivate
        temp['is_active'] = followerUser.is_active
        try:
            userPhoto = Image.objects.filter(user = user.id).first()
            temp['userPhoto'] = userPhoto.file.url
        except:
            temp['userPhoto'] = ""
        followers.append(temp)
    followed = []
    for followedUser in user.followedUsers.all():
        temp = {}
        temp['id'] = followedUser.id
        temp['username'] = followedUser.username
        temp['name'] = followedUser.name
        temp['surname'] = followedUser.surname
        temp['email'] = followedUser.email
        temp['isBanned'] = followedUser.isBanned
        temp['isAdmin'] = followedUser.isAdmin
        temp['isPrivate'] = followedUser.isPrivate
        temp['is_active'] = followedUser.is_active
        try:
            userPhoto = Image.objects.filter(user = user.id).first()
            temp['userPhoto'] = userPhoto.file.url
        except:
            temp['userPhoto'] = ""
        followed.append(temp)
    serializer['followerUsers'] = followers
    serializer['followedUsers'] = followed
    try:
        userPhoto = Image.objects.filter(user = user.id).first()
        serializer['userPhoto'] = userPhoto.file.url
    except:
        serializer['userPhoto'] = ""
    serializer['id'] = user.id
    return serializer