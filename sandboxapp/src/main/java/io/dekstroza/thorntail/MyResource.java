package io.dekstroza.thorntail;

import okhttp3.OkHttpClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class MyResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        //If this is not present and pom file scope for okhttp is just test, then
        //test fails with class not found OkHttpClient!
        OkHttpClient httpClient = new OkHttpClient();
        return "Hello World";
    }
}

