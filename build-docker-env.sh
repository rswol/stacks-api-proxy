#!/bin/bash

# build proxy UI
docker build ui -f ui/Dockerfile -t stacks-api-proxy-ui

# build proxy UI/API frontend
docker build ui -f ui/Dockerfile.nginx -t stacks-api-proxy-nginx  

# build proxy
docker build target -f Dockerfile -t stacks-api-proxy

