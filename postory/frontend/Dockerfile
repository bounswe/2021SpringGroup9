# pull official base image
FROM node:13.12.0-alpine

# set working directory
WORKDIR /app

# add `/app/node_modules/.bin` to $PATH
ENV PATH /app/node_modules/.bin:$PATH
ARG REACT_APP_GOOGLE_API_KEY

# install app dependencies
COPY package.json ./
COPY package-lock.json ./
RUN npm install --silent
RUN npm install serve --silent

# add app
COPY . .
RUN REACT_APP_GOOGLE_API_KEY=$REACT_APP_GOOGLE_API_KEY npm run build


# start app
CMD serve -s build
