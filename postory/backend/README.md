# HOW TO RUN LOCALLY

First, clone our github repository. Then, put the ".env" file that we provided to this path. You should be on /postory/backend path. Run:

```
docker compose up
```

If you want to run in detach mode. You can use following command:

```
docker compose up -d
```

It will start to run on port 8000.

# HOW TO DEPLOY

First, clone our github repository. You should be on /postory/backend path. You should login to docker repository. It will ask username and password. Username is also repository name. Login command:

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

Now, the docker image is on our docker repository and we can deploy it wherever we want. The database image should be deployed before our application. The dump restoration should be made after database image is created. While deploying our application the environment variables should be pass. You should change the environment variables according the new deployment environment. Following commands should be executed in order to deploy backend.

```
docker run -d --restart always -p 2717:27017 --volume mongo_database:/data/db --env MONGO_INITDB_ROOT_USERNAME=<MONGO_USERNAME> --env MONGO_INITDB_ROOT_PASSWORD=<MONGO_PASSWORD> --name mongo mongo:latest
docker network create myNetwork
docker network connect myNetwork mongo
docker cp </file/path/to/dump/directory> <MONGO-CONTAINER-ID>:/
docker exec -it <MONGO-CONTAINER-ID> bash
    mongorestore -u <MONGO_USERNAME> -p <MONGO_PASSWORD>
    exit
docker run -d --restart always -p 8000:8000 --network myNetwork --name backend \
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
              
After executing these commands, backend will start to run on the machine with the restored dump.

IMPORTANT NOTE: The dump restoration should be made after the mongo container created.

# DEPLOY USING GITHUB

You can use GitHub for deployment by using workflows. If you change the secret variables of this [workflow](https://github.com/bounswe/2021SpringGroup9/blob/master/.github/workflows/backend_deploy.yml), you can deploy the backend to an instance.

## ENVIRONMENT VARIABLES

* SECRET_KEY: Django secret key.
* MONGO_USERNAME: Username for mongo.
* MONGO_PASSWORD: Password for mongo.
* GOOGLE_MAPS_API_KEY: API key for google maps.
* MONGO_CONTAINER_NAME: Mongo host name. It should be mongo.
* MONGO_PORT: Mongo port. It should be 27017.
* AWS_ACCESS_KEY_ID: AWS User access key.
* AWS_SECRET_ACCESS_KEY: AWS User secret key.
* AWS_STORAGE_BUCKET_NAME: AWS S3 bucket name.
* EMAIL: System email.
* EMAIL_PASSWORD: Email password for the system email.
* HOST: The IP of the host machine.
