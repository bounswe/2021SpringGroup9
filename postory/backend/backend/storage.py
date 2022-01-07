from django.conf import settings
from storages.backends.s3boto3 import S3Boto3Storage


class ImageStorage(S3Boto3Storage):
    location = 'image'
    default_acl = 'public-read'
    file_overwrite = False