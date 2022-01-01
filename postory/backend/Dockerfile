FROM python:3.6
ENV PYTHONUNBUFFERED=1
WORKDIR /code
COPY requirements.txt /code/
RUN pip install --upgrade pip && pip install -r requirements.txt
COPY . /code/
CMD python manage.py makemigrations post_endpoint user_endpoint activityStream && python manage.py migrate && python manage.py runserver 0.0.0.0:8000