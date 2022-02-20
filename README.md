# stacks-api-proxy

Stacks API Node proxy is a simple application based on https://github.com/Kong/kong API Gateway.
In addition to API Gateway it includes control plan app "stacks-api-proxy" that configures 
the gateway and checks the health of underlying endpoints. 

The proxy periodically probes endpoints and if they are not available or fall behind the endpoint
will be excluded from the backend set.

The gateway accepts requests on port 8000 and routes them to the healthy backend endpoints:
```bash
$ curl http://localhost:8000/extended/v1/block
{
  "limit": 20,
  "offset": 0,
  "total": 49602,
  "results": [
    {
      "canonical": t
...      
```

Authentication
---
Authentication with JWT is optional. By default, it is off. To enable run the following command:
```bash
$ curl -X POST http://localhost:8088/v1/authOn 
```

Once it is enabled, every request to the gateway will be rejected unless JWT token is not added:
```bash
$ curl http://localhost:8000/extended/v1/block                                                            
{"message":"Unauthorized"}           
```

To obtain JWT for "stacks" call proxy:
```bash
$ curl -X POST -H "Content-Type: application/json" \ 
      -d '{"user": "stacks"}' http://localhost:8088/v1/jwt
```

To disable authentication:
```bash
$ curl -X POST http://localhost:8088/v1/authOff 
```


How to start the stacks-api-proxy application
---

1. Run `mvn clean install` to build proxy jar
2. Run `docker build . -f Dockerfile -t stacks-api-proxy` to create a docker image
3. Edit `config/docker.yml` to add your Stacks API Node endpoints
4. Start application with `docker-compose up`

The API load balancer will be running off the port 8000 (e.g. http://localhost:8000/extended/v1/block)

To restart the proxy (and re-configure the gateway) after changing config/docker.yml run the following:
```bash
$ docker-compose restart proxy
```