# HOW TO RUN LOCALLY

First, clone our github repository. Then, put the ".env" file that we provided to this path. You should be on this path as well. Run:

```
docker compose up
```

If you want to run in detach mode. You can use following command:

```
docker compose up -d
```

It will start to run on port 8000.

# HOW TO DEPLOY

First, you should login to docker repository. It will ask username and password. Username is also repository name. Login command:

```
docker login
```

Then, you should build the docker file by using following command:

```
docker build -t <REPOSITORY-NAME>/backend:latest
```

After building, the docker image should be pushed to docker repository by using following command:

```
docker push <REPOSITORY-NAME>/backend:latest
```

Now, the docker image is on our docker repository and we can deploy it wherever we want. The database image should be deployed before our application. While deploying our application the environment variables should be pass. You should change the environment variables according the new deployment environment. Following commands should be executed in order to deploy backend:

```
docker run -d --restart always -p 2717:27017 --volume mongo_database:/data/db --env MONGO_INITDB_ROOT_USERNAME=<MONGO_USERNAME> --env MONGO_INITDB_ROOT_PASSWORD=<MONGO_PASSWORD> --name mongo mongo:latest
docker run -d --restart always -p 8000:8000 --link mongo --name backend \
              --env MONGO_USERNAME=<MONGO_USERNAME }} \
              --env MONGO_PASSWORD=<MONGO_PASSWORD }} \
              --env SECRET_KEY="<SECRET_KEY>" \
              --env GOOGLE_MAPS_API_KEY=<GOOGLE_MAPS_API_KEY> \
              --env MONGO_CONTAINER_NAME=mongo \
              --env MONGO_PORT=27017 \
              --env AWS_ACCESS_KEY_ID=<AWS_ACCESS_KEY_ID> \
              --env AWS_SECRET_ACCESS_KEY=<AWS_SECRET_ACCESS_KEY> \
              --env AWS_STORAGE_BUCKET_NAME=<AWS_STORAGE_BUCKET_NAME> \
              --env EMAIL=<EMAIL> \
              --env EMAIL_PASSWORD=<EMAIL_PASSWORD> \
              --env HOST=<HOST> \
              <REPOSITORY-NAME>/backend:latest
```
              
After executing these commands, backend will start to run on the machine.
