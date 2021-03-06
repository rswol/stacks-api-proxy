package com.stacksonchain.resources;

import com.stacksonchain.core.JwtMaker;
import com.stacksonchain.core.KongConfigurator;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class AdminApiResource {

  @Inject
  JwtMaker jwtMaker;

  @Inject
  KongConfigurator kong;

  @GET
  @Path("/users")
  public List<String> users() {
    return jwtMaker.listUsers();
  }

  @POST
  @Path("/jwt")
  public String jwt(JwtRequest request) {
    return jwtMaker.userJwt(request.user);
  }

  @DELETE
  @Path("/jwt")
  public String jwtDelete(JwtRequest request) {
    jwtMaker.deleteUserJwt(request.user);
    return "ok";
  }

  @POST
  @Path("/authOn")
  public String authOn() {
    kong.authOn();
    return "ok";
  }

  @GET
  @Path("/isAuth")
  public Boolean isAuth() {
    return kong.isAuth();
  }

  @POST
  @Path("/authOff")
  public String authOff() {
    kong.authOff();
    return "ok";
  }
}
