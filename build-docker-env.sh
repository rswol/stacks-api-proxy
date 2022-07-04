#!/bin/bash


docker build ui -f ui/Dockerfile -t stacks-api-proxy-ui
docker build . -f ui/Dockerfile.nginx -t stacks-api-proxy-nginx  

