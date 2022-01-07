# Building and running the app
The app can be built and run with docker compose. 

## Requirements

* Docker _(available [here](https://docs.docker.com/get-docker/)_)
* docker-compose _(available [here](https://docs.docker.com/compose/install/)_)
* git _(available [here](https://git-scm.com/downloads)_)
* The provided .env file of the frontend.
* Change the `REACT_APP_BACKEND_API` variable on the .env file if needed.

## Build and run the app using docker compose
You first should have a configuration file named .env (on the same directory as the docker-compose.yml file - /2021SpringGroup9/postory/frontend) that has the necessary lines:

```
REACT_APP_GOOGLE_API_KEY=<Your google maps javascript api key> 
REACT_APP_BACKEND_API=<Ip of the backend. Example: 3.67.83.253>
```
We are going to provide the file so just copy it into 2021SpringGroup9/postory/frontend.\
Open up a terminal in the same directory.


Then you can run the command (on the same directory as the docker-compose.yml file) to run your application:

```
docker-compose up
```

If you want to run the application again after changing the .env variables please run these commands:
```
docker-compose build
docker-compose up
```




After running these commands, the frontend will be available at: http://localhost:3000/. 


## Build and run without docker-compose
### Build the image
```
docker build -t frontend --build-arg REACT_APP_GOOGLE_API_KEY=<Your-GOOGLE-MAPS-JAVASCRIPT-API-KEY> --build-arg REACT_APP_BACKEND_API=<IP-ADDRESS-OF-BACKEND> .
```
### Run the image
```
docker run -p 3000:3000 frontend
```


# Running the application without docker.

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.\
You will also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can’t go back!**

If you aren’t satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you’re on your own.

You don’t have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn’t feel obligated to use this feature. However we understand that this tool wouldn’t be useful if you couldn’t customize it when you are ready for it.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).

### Code Splitting

This section has moved here: [https://facebook.github.io/create-react-app/docs/code-splitting](https://facebook.github.io/create-react-app/docs/code-splitting)

### Analyzing the Bundle Size

This section has moved here: [https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size](https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size)

### Making a Progressive Web App

This section has moved here: [https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app](https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app)

### Advanced Configuration

This section has moved here: [https://facebook.github.io/create-react-app/docs/advanced-configuration](https://facebook.github.io/create-react-app/docs/advanced-configuration)

### Deployment

This section has moved here: [https://facebook.github.io/create-react-app/docs/deployment](https://facebook.github.io/create-react-app/docs/deployment)

### `npm run build` fails to minify

This section has moved here: [https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify](https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify)
