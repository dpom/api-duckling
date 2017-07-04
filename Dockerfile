FROM clojure:alpine

RUN mkdir -p /usr/src/app

WORKDIR /usr/src/app

# COPY project.clj /usr/src/app/

# RUN lein deps

ENV PORT 3000

EXPOSE $PORT

COPY . /usr/src/app

CMD lein ring server-headless $PORT 
