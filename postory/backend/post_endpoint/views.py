from django.http import Http404
from django.http import HttpResponse

from django.shortcuts import render
from django.urls.base import resolve
from rest_framework.generics import GenericAPIView
from rest_framework.response import Response

from rest_framework import status
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView
from rest_framework.decorators import api_view

import urllib.parse as urlparse
from urllib.parse import urlencode

from .models import Post, Location, Tag, Image, Comment
from .serializers import PostSerializer
from user_endpoint.models import User
from django.utils import timezone
import pytz

from django.db.models import QuerySet
import requests
import json
import datetime
import environ
import jwt
import string
import re
from geopy import distance

import activityStream.views as activityStream
from django.urls import reverse

class GetAllPosts(GenericAPIView):
    """
        Get all posts of every user.
        Can be used by admin only.
    """
    serializer_class = PostSerializer

    def get(self, request, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        user = User.objects.filter(id = user_id).first()
        if(user.isAdmin):
            posts = Post.objects.all()
            serializer = {}
            for story in posts:
                serializer[story.id] = get_story(story)
            activityStream.createActivity(user.id,"requested all posts","allPosts",resolve(request.path_info).route,"PostAllRequest",True)
            return Response(serializer.values(), status=200)
        else:
            activityStream.createActivity(user.id,"requested all posts","allPosts",resolve(request.path_info).route,"PostAllRequest",False)
            return Response(status = 401)

env = environ.Env(DEBUG=(bool, True))
environ.Env.read_env('.env')

class PostCreate(GenericAPIView):
    """
    Create a post.
    """
    serializer_class = PostSerializer

    def post(self, request, format=None):
        userid = request.auth['user_id']
        data = dict(request.data)
        print(data)
        data['title'] = data['title'][0]
        data['story'] = data['story'][0]
        data['owner'] = userid
        if "year" in data:
            data['year'] = data['year'][0] + ',' + data['year'][1]
        if "month" in data:
            data['month'] = data['month'][0] + ',' + data['month'][1]
        if "day" in data:
            data['day'] = data['day'][0] + ',' + data['day'][1]
        if "hour" in data:
            data['hour'] = data['hour'][0] + ',' + data['hour'][1]
        if "minute" in data:
            data['minute'] = data['minute'][0] + ',' + data['minute'][1]

        
        images = data['images']
        imagesList = []
        for image in images:
            if image:
                imageObject = Image(file=image)
                imageObject.save()
                imagesList.append(imageObject)
        data['images'] = [image.id for image in imagesList]
         
        locations = data['locations']
        locationsList = []
        for location in locations:
            if location:
                location = json.loads(location)
                locationObject = Location(name=location["name"], coordsLatitude=location["latitude"], coordsLongitude=location["longitude"])
                locationsList.append(locationObject)
                locationObject.save()
        data['locations'] = [location.id for location in locationsList]

        tags = data['tags']
        tagsList = []
        for tag in tags:
            if tag:
                tag = str(tag).lower()
                search = Tag.objects.filter(content=tag).first()
                if(search):
                    tagsList.append(search)
                    continue
                tagObject = Tag(content=tag)
                tagObject.save()
                tagsList.append(tagObject)
        data['tags'] = [tag.id for tag in tagsList]
        user = User.objects.get(pk = userid)
        data['username'] = user.username
        serializer = PostSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            story = Post.objects.get(pk = serializer.data["id"])
            user.posts.add(story)
            activityStream.createActivity(user.id,"created a post",story.id,resolve(request.path_info).route,"PostCreate",True)
            return Response(get_story(story), status=200)
        activityStream.createActivity(user.id,"created a post","None",resolve(request.path_info).route,"PostCreate",False)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class PostListDetail(GenericAPIView):
    """
    Retrieve a story instance.
    """

    def get_object(self, pk):
        try:
            return Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']

        user1 = User.objects.filter(id=user_id).first()
        story = self.get_object(pk)
        userid2 = story.owner
        user2 = User.objects.filter(pk=userid2).first()
        
        # only if the user is admin or is a follower of the post owner or it is the post owner or the post is public
        if not user2.isPrivate or user1.isAdmin or (user1 in user2.followerUsers.all()) or (user1.id == user2.id):
            serializer = get_story(story)
            activityStream.createActivity(user1.id,"requested post",story.id,resolve(request.path_info).route,"PostRequest",True)
            return Response(serializer, status=200)
        else:
            activityStream.createActivity(user1.id,"requested post",story.id,resolve(request.path_info).route,"PostRequest",False)
            return Response(status=status.HTTP_401_UNAUTHORIZED)
        
class PostUpdate(GenericAPIView):
    """
    Update a story instance.
    """

    def get_object(self, pk):
        try:
            return Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404
    
    def put(self, request, pk, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']

        user1 = User.objects.filter(id=user_id).first()
        story = self.get_object(pk)
        user2 = story.owner

        # update only if the user owns the post
        if (user1.id==user2):
            data = dict(request.data)
            data['title'] = data['title'][0]
            data['story'] = data['story'][0]
            data['owner'] = user_id
            if "year" in data:
                data['year'] = data['year'][0] + ',' + data['year'][1]
            if "month" in data:
                data['month'] = data['month'][0] + ',' + data['month'][1]
            if "day" in data:
                data['day'] = data['day'][0] + ',' + data['day'][1]
            if "hour" in data:
                data['hour'] = data['hour'][0] + ',' + data['hour'][1]
            if "minute" in data:
                data['minute'] = data['minute'][0] + ',' + data['minute'][1]
            
            images = data['images']
            imagesList = []
            for image in images:
                if image:
                    imageObject = Image(file=image)
                    imageObject.save()
                    imagesList.append(imageObject)
            data['images'] = [image.id for image in imagesList]
            
            locations = data['locations']
            locationsList = []
            for location in locations:
                if location:
                    location = json.loads(location)
                    locationObject = Location(name=location["name"], coordsLatitude=location["latitude"], coordsLongitude=location["longitude"])
                    locationsList.append(locationObject)
                    locationObject.save()
            data['locations'] = [location.id for location in locationsList]

            tags = data['tags']
            tagsList = []
            for tag in tags:
                if tag:
                    tag = str(tag).lower()
                    search = Tag.objects.filter(content=tag).first()
                    if(search):
                        tagsList.append(search)
                        continue
                    tagObject = Tag(content=tag)
                    tagObject.save()
                    tagsList.append(tagObject)
            data['tags'] = [tag.id for tag in tagsList]

            serializer = PostSerializer(story, data=data)
            if serializer.is_valid():
                serializer.save(editDate=timezone.now())
                story = self.get_object(pk)
                activityStream.createActivity(user1.id,"updated post",story.id,resolve(request.path_info).route,"PostUpdate",True)
                return Response(get_story(story), status=200)
            activityStream.createActivity(user1.id,"updated post",story.id,resolve(request.path_info).route,"PostUpdate",False)
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        else:
            activityStream.createActivity(user1.id,"updated post",story.id,resolve(request.path_info).route,"PostUpdate",False)
            return Response(status=status.HTTP_401_UNAUTHORIZED)
    
class PostDelete(GenericAPIView):
    """
    Delete a story instance.
    """

    def get_object(self, pk):
        try:
            return Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404
    
    def delete(self, request, pk, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']

        story = self.get_object(pk)

        if(not user_id == story.owner):
            activityStream.createActivity(user_id,"deleted post",story.id,resolve(request.path_info).route,"PostDelete",False)
            return Response(status = 401)
        activityStream.createActivity(user_id,"deleted post",story.id,resolve(request.path_info).route,"PostDelete",True)
        story.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
    
class GetUsersPosts(GenericAPIView):
    """
        Return posts of specific user given in the url.
    """
    def get(self, request, user_id, format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        requester_user_id = decoded['user_id']

        requester_user = User.objects.filter(id = requester_user_id).first()
        requested_user = User.objects.filter(id = user_id).first()

        if(requested_user in requester_user.followedUsers.all() or not requested_user.isPrivate or requested_user.id == requester_user.id):
            posts = Post.objects.filter(owner = requested_user.id)
            serializer = {}
            for story in posts:
                serializer[story.id] = get_story(story)
            activityStream.createActivity(requester_user.id,"requested posts of",requested_user.id,resolve(request.path_info).route,"PostRequestOfUser",True)
            return Response(serializer.values(), status=200)
        else:
            activityStream.createActivity(requester_user.id,"requested posts of",requested_user.id,resolve(request.path_info).route,"PostRequestOfUser",False)
            return Response(status = 401)


class GetFollowedUsersPosts(GenericAPIView):
    """
        Return posts of followed users' posts. 
        This is used for main page posts.
    """
    def get(self,request,format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        user = User.objects.filter(id = user_id).first()
        followedUsers = user.followedUsers.all()
        posts = []
        for followedUser in followedUsers:
            followedUserPosts = Post.objects.filter(owner = followedUser.id)
            for post in followedUserPosts:
                posts.append(post)
        for post in Post.objects.filter(owner = user_id):
            posts.append(post)
        serializer = {}
        for story in posts:
            serializer[story.id] = get_story(story)
        activityStream.createActivity(user_id,"requested posts of followed","allFollowed",resolve(request.path_info).route,"PostRequestOfFollowed",True)
        return Response(serializer.values(), status=200)


class GetPostsDiscover(GenericAPIView):
    """
        Return public users' and followed users' posts.
        This is used for discovery page.
    """
    def get(self,request,format=None):
        authorization = request.headers['Authorization']
        token = authorization.split()[1]
        decoded = jwt.decode(token,options={"verify_signature": False})
        user_id = decoded['user_id']
        user = User.objects.filter(id = user_id).first()
        followedUsers = user.followedUsers.all()
        posts = set()
        for post in Post.objects.all():
            owner_of_post = User.objects.get(id=post.owner)
            if(not owner_of_post.isPrivate):
                posts.add(post)
        for followedUser in followedUsers:
            followedUserPosts = Post.objects.filter(owner = followedUser.id)
            for post in followedUserPosts:
                posts.add(post)
        serializer = {}
        for story in posts:
            serializer[story.id] = get_story(story)
        activityStream.createActivity(user_id,"requested posts for discover","allDiscover",resolve(request.path_info).route,"PostRequestDiscover",True)
        return Response(serializer.values(), status=200)

class CommentRequest(GenericAPIView):
    """
    Creates a comment for specified story id.
    """ 
    def get_object(self, pk):
        try:
            return Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404

    def post(self, request, pk, format=None):
        userid = request.auth['user_id']
        data = dict(request.data)
        
        comment = data['comment']
        if comment:
            commentObject = Comment(userid=userid, comment=comment)
            commentObject.save()
        else:
            return    

        story = self.get_object(pk)
        commentList = list(story.comments.all())
        commentList.append(commentObject.id)
        story.comments.set(commentList)
        
        try:
            story.save()
            activityStream.createActivity(userid,"commented on post",story.id,resolve(request.path_info).route,"PostComment",True)
            return Response(get_story(story), status=200)
        except:
            activityStream.createActivity(userid,"commented on post",story.id,resolve(request.path_info).route,"PostComment",False)
            return Response(status=status.HTTP_400_BAD_REQUEST)
        
class LikeRequest(GenericAPIView):
    """
    Likes the post of specified story id
    """
    def get_object(self, pk):
        try:
            return Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404

    def post(self, request, pk, format=None):
        userid = request.auth['user_id']
        user = User.objects.get(id = userid)

        story = self.get_object(pk)
        likeList = story.likeList.split(',')
        if likeList[0] == "":
            likeList = []
        if str(userid) in likeList:
            user.likedPosts.remove(story)
            likeList.remove(str(userid))
        else:
            user.likedPosts.add(story)
            likeList.append(str(userid))
        story.likeList = ','.join(likeList)
        
        try:
            story.save()
            activityStream.createActivity(userid,"liked post",story.id,resolve(request.path_info).route,"PostLike",True)
            return Response(get_story(story), status=200)
        except:
            activityStream.createActivity(userid,"liked post",story.id,resolve(request.path_info).route,"PostLike",False)
            return Response(status=status.HTTP_400_BAD_REQUEST)
        
class GetPostsDiscoverFilter(GenericAPIView):
    """
    Returns the filtered posts.
    Posts are filtered according to their user, title, story, time, location and tag.
    Related tags can be found by using wikidata.
    This is used for discovery page.
    """
    def get(self,request,format=None):
        # users = User.objects.filter(username__contains=term)
        userid = request.auth['user_id']
        user = User.objects.get(id = userid)
        followedUsers = user.followedUsers.all()
        followedUsers = [followedUser.id for followedUser in followedUsers]
        query_parameters = dict(request.query_params)
        
        posts = Post.objects.all().distinct()
            
        # Username
        if 'user' in query_parameters:
            usernames = query_parameters['user']
            for post in posts:
                user = User.objects.get(id=post.owner)
                if user.username not in usernames:
                    posts = posts.exclude(id = post.id)
            
        # Time
        startYear, endYear, startMonth, endMonth, startDay, endDay, startHour, endHour, startMinute, endMinute = None, None, None, None, None, None, None, None, None, None
        if 'startYear' in query_parameters:    
            startYear = int(query_parameters['startYear'][0])
            if 'endYear' in query_parameters:
                endYear = int(query_parameters['endYear'][0])
            else:
                endYear = datetime.datetime.now().year
            if 'startMonth' in query_parameters:
                startMonth = int(query_parameters['startMonth'][0])
                if 'endMonth' in query_parameters:
                    endMonth = int(query_parameters['endMonth'][0])
                else:
                    endMonth = 12
                if 'startDay' in query_parameters:
                    startDay = int(query_parameters['startDay'][0])
                    if 'endDay' in query_parameters:
                        endDay = int(query_parameters['endDay'][0])
                    else:
                        endDay = 31
                    if 'startHour' in query_parameters:
                        startHour = int(query_parameters['startHour'][0])
                        if 'endHour' in query_parameters:
                            endHour = int(query_parameters['endHour'][0])
                        else:
                            endHour = 24
                        if 'startMinute' in query_parameters:
                            startMinute = int(query_parameters['startMinute'][0])
                            if 'endMinute' in query_parameters:
                                endMinute = int(query_parameters['endMinute'][0])
                            else:
                                endMinute = 60
            for post in posts:
                year = [int(x) for x in post.year.split(',')] if post.year != '' else []
                month = [int(x) for x in post.month.split(',')] if post.month != '' else []
                day = [int(x) for x in post.day.split(',')] if post.day != '' else []
                hour = [int(x) for x in post.hour.split(',')] if post.hour != '' else []
                minute = [int(x) for x in post.minute.split(',')] if post.minute != '' else []
                if year and startYear and endYear:
                    if year[0] > endYear or year[1] < startYear: 
                        posts = posts.exclude(id = post.id)
                if month and startMonth and endMonth:
                    if month[0] > endMonth or month[1] < startMonth:
                        posts = posts.exclude(id = post.id)
                if day and startDay and endDay:
                    if day[0] > endDay or day[1] < startDay:
                        posts = posts.exclude(id = post.id)
                if hour and startHour and endHour:
                    if hour[0] > endHour or hour[1] < startHour:
                        posts = posts.exclude(id = post.id)
                if minute and startMinute and endMinute:
                    if minute[0] > endMinute or minute[1] < startMinute:
                        posts = posts.exclude(id = post.id)
                        
        # Location
        if 'latitude' in query_parameters and 'longitude' in query_parameters and 'distance' in query_parameters:
            latitude = query_parameters['latitude'][0].replace(',', '.')
            longitude = query_parameters['longitude'][0].replace(',', '.')
            distance = query_parameters['distance'][0].replace(',', '.')
            posts = getQuerySetOfNearby(posts, latitude, longitude, distance)
            
        # Tag
        if 'related' in query_parameters:
            related = query_parameters['related'][0]
        else:
            related = 'true'
        if 'tag' in query_parameters:
            tags = query_parameters['tag']
            all_tags = []
            if related.lower() == 'true':
                for tag in tags:
                    all_tags = getRelatedTags(tag, all_tags)
            else:
                all_tags = tags
            for post in posts:
                hasTag = False
                for post_tag in post.tags.all():
                    if post_tag.content in all_tags:
                        hasTag = True
                if hasTag == False:
                    posts = posts.exclude(id = post.id)
            
        # Keyword
        if 'keyword' in query_parameters:
            keywords = query_parameters['keyword'][0].split()
            for index, keyword in enumerate(keywords):
                if index == 0:
                    posts_story = posts.filter(story__icontains=keyword)
                else:
                    posts_story = (posts_story | posts.filter(story__icontains=keyword)).distinct()
            for index, keyword in enumerate(keywords):
                if index == 0:
                    posts_title = posts.filter(title__icontains=keyword)
                else:
                    posts_title = (posts_title | posts.filter(title__icontains=keyword)).distinct()
            post_story_title = (posts_story | posts_title).distinct()
            posts = (posts & post_story_title)
                        
        posts_set = set()
        for post in posts:
            owner_of_post = User.objects.get(id=post.owner)
            if owner_of_post.isPrivate == False:
                posts_set.add(post)
            elif owner_of_post.id == userid:
                posts_set.add(post)
            elif owner_of_post.isPrivate == True and owner_of_post.id in followedUsers:
                posts_set.add(post) 
        serializer = []
        for story in posts_set:
            serializer.append(get_story(story))
        activityStream.createActivity(userid,"filtered posts","allFilter",resolve(request.path_info).route,"PostFilter",True)
        return Response(serializer, status=200)

class GetRelatedTags(GenericAPIView):
    """
    Gets related for speficied keyword from wikidata.
    """
    def get(self, request, query, format=None):
        userid = request.auth['user_id']
        if request.auth:
            try:
                tag_list = list(getRelatedTags(query, None))
                activityStream.createActivity(userid,"got related tags","related",resolve(request.path_info).route,"GetRelatedTags",True)
                return Response(tag_list, status = 200)
            except:
                activityStream.createActivity(userid,"got related tags","related",resolve(request.path_info).route,"GetRelatedTags",False)
                return Response(status = 503)
        else:
            activityStream.createActivity(userid,"got related tags","related",resolve(request.path_info).route,"GetRelatedTags",False)
            return Response(status = 401)
        
class SavePost(GenericAPIView):
    """
    Saves story for user.
    The story id is stored in user.
    """
    def post(self, request, pk, format=None):
        userid = request.auth['user_id']
        user = User.objects.get(id = userid)
        try:
            post = Post.objects.get(id = pk)
        except Post.DoesNotExist:
            raise Http404 
        
        if post in user.savedPosts.all():
            activityStream.createActivity(userid,"saved post",post.id,resolve(request.path_info).route,"PostSave",True)
            user.savedPosts.remove(post)
        else:
            activityStream.createActivity(userid,"unsaved post",post.id,resolve(request.path_info).route,"PostSave",True)
            user.savedPosts.add(post)
        return Response(status=200)
        
"""
Returns related tags from wiki data.
Multiple keywords can be passed.
"""      
def getRelatedTags(query, all_tags):
    if all_tags == None:
        all_tags = []
    set_tags = set(all_tags)
    url = "https://www.wikidata.org/w/api.php"
    params = {
        "action" : "wbsearchentities",
        "language" : "en",
        "format" : "json",
        "type": "item",
        "search" : query
        }
    data = requests.get(url,params=params).json()
    id = data['search'][0]['id']
    
    params = {
        "action" : "wbgetentities",
        "format" : "json",
        "languages" : "en",
        "ids": id
    }
    
    data = requests.get(url,params=params).json()
    for alias in data['entities'][id]['aliases']['en']:
        try:
            words = alias['value'].lower()
            if words not in set_tags:
                all_tags.append(alias['value'].lower())
                set_tags.add(words)
        except:
            pass
        
    claims = []
    for claim_id in data['entities'][id]['claims'].values():
        for value in claim_id:
            try:
                claims.append(value['mainsnak']['datavalue']['value']['id'])   
            except:
                pass
                    
    params = {
        "action" : "wbgetentities",
        "format" : "json",
        "languages" : "en",
        "ids": "|".join(claims[0:9])
    }
    data = requests.get(url,params=params).json()
    
    if query.lower() not in set_tags:
        all_tags.append(query.lower())
        set_tags.add(query.lower())
    for entity in data['entities'].values():
        try:
            words = entity['labels']['en']['value'].lower()
            if words.startswith('category:'):
                words = words.replace('category:', '')
            if words.startswith('template:'):
                words = words.replace('template:', '')
            if words.startswith('wikipedia:'):
                words = words.replace('wikipedia:', '')
            if words not in set_tags:
                all_tags.append(words)
                set_tags.add(words)
            if words.startswith(query + ' '):
                words = words.replace(query + ' ', '')
            if words not in set_tags:
                all_tags.append(words)
                set_tags.add(words)
        except:
            pass
        
    return all_tags

"""
Gets the query set of nearby stories.
This is used in filter function.
"""
def getQuerySetOfNearby(posts, latitude, longitude, distance):
    requestedDistance = float(distance)
    pinPoint = [float(latitude),float(longitude)]
    for story in posts:
        locations = story.locations.all()
        toBeReturned = False
        for location in locations:
            distanceBetween = getDistanceBetween([location.coordsLatitude, location.coordsLongitude],pinPoint)
            if(distanceBetween < requestedDistance):
                toBeReturned = True
                break
        if(not toBeReturned):
            posts = posts.exclude(id = story.id)
    return posts
def getDistanceBetween(loc1,loc2):
    lat1 = loc1[0]
    lon1 = loc1[1]
    lat2 = loc2[0]
    lon2 = loc2[1]
    return distance.distance((lat1,lon1),(lat2,lon2)).km

"""
Return the detailed information about story object.
In some fields, the id of other objects are stored. They are retrieved by this function.
All the information about the story object is returned as JSON format.
"""
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
    return serializer