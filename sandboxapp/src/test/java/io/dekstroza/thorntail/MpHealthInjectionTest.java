package io.dekstroza.thorntail;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.WAR)
public class MpHealthInjectionTest {

    /**
     * Use okhttp client to externalise call
     * or curl -v http://localhost:8080/health
     */
    @Test
    public void testHealthCheckEndpoint() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://localhost:8080/health").build();
        Response response = client.newCall(request).execute();
        Assert.assertEquals(200, response.code());
    }

    /**
     *
     * Fails as well, produces different error:
     * Caused by: java.lang.ClassNotFoundException: org.glassfish.jersey.client.JerseyClientBuilder from [Module "javax.ws.rs.api" from BootModuleLoader@548e6d58 for finders [BootstrapClasspathModuleFinder, BootstrapModuleFinder(org.wildfly.swarm.bootstrap), ClasspathModuleFinder, ContainerModuleFinder(swarm.container), ApplicationModuleFinder(swarm.application), org.wildfly.swarm.bootstrap.modules.DynamicModuleFinder@623a8092]]
     */
//    @Test
//    public void testHealthCheckEndpointWithMP() throws Exception {
//        final HealthAPI healthAPI = RestClientBuilder.newBuilder().baseUrl(new URL("http://localhost:8080")).build(HealthAPI.class);
//        Assert.assertEquals(200, healthAPI.getHealth().getStatus());
//    }
}
