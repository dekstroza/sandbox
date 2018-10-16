package io.dekstroza;

import io.dekstroza.repository.cdi.annotations.CassandraConfig;
import io.thorntail.Thorntail;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@CassandraConfig(contact_points = { "127.0.0.1" })
@ApplicationPath("/")
public class MyApplication extends Application {
    public static void main(String... args) throws Exception {
        Thorntail.run();
    }
}
