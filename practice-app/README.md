# Practice-app

* You should have the environment file to run this application and the environment file should be in the same directory as Docker files. After that, to build and run this application make sure that you are on the same directory as Docker files. Then, run this command:

  `docker compose up`

* If you want to run the application in background, run this command:

  `docker compose up -d`

* It will build and create containers and then run the application on localhost. You can access the application by visiting http://localhost:8000/ since it uses port 8000.

* You can stop the application by using the following command:

  `docker compose down`
  
## Important Note

* After running `docker compose up` command you can see an error similar to this:

  `django.db.utils.OperationalError: (2002, "Can't connect to MySQL server on 'db' (115)").`
  
 * The solution is waiting. Because the database cannot initialize fast enough and our web application tries to connect to it, this error occurs. The application does not give up until it connects. Thus, after the database is initialized, it will connect successfully and it will start working.

## Last Note

* Make sure that MySQL server on your machine is not connected to 3306. If that is the case, disconnect because the container cannot use your machine's 3306 port for database.
