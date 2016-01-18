package introsde.mandrillemail;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("mandrill-email")
public class ServiceConfig extends ResourceConfig {
    public ServiceConfig () {
        packages("introsde.mandrillemail");
    }
}
