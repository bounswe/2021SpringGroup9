from django.http import Http404
from django.http import HttpResponse

from django.shortcuts import render
from rest_framework.generics import GenericAPIView
from rest_framework.response import Response

from rest_framework import status
from rest_framework.response import Response
from rest_framework.generics import GenericAPIView

from .models import User
from .serializers import UserSerializer
from post_endpoint.models import Post, Image
from post_endpoint.serializers import PostSerializer

class AddPhoto(GenericAPIView):
    def get_object(self, pk):
        try:
            return User.objects.get(pk=pk)
        except Post.DoesNotExist:
            raise Http404

    def post(self, request, format=None):
        userid = request.auth['user_id']
        data = dict(request.data)
        
        images = data['images']
        imagesList = []
        for image in images:
            if image:
                imageObject = Image(file=image)
                imageObject.save()
                imagesList.append(imageObject.id)
        
        user = self.get_object(userid)
        user.images.set(imagesList)
    
        try:
            user.save()
        except:
            return Response(status=status.HTTP_400_BAD_REQUEST)
        
class SearchUser(GenericAPIView):

    def post(self, request, term, format=None):
        users = User.objects.filter(username__contains=term)
        values = []
        for user in users:
            data = dict(UserSerializer(user).data)
            values.append(data)
        return Response(values, status=200)
        
def get_user(user):
    # Blog.objects.filter(pk__in=[1, 4, 7])
    pass
    