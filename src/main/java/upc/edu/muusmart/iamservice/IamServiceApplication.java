package upc.edu.muusmart.iamservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the IAM (Identity and Access Management) microservice.
 *
 * <p>This class bootstraps the Spring Boot application. It enables component
 * scanning under the {@code upc.edu.muusmart.iamservice} package so that Spring
 * will detect and register controllers, services, repositories and other
 * components defined in this microservice. Running the main method will
 * start an embedded Tomcat server on the default port (8080) and make the
 * REST endpoints available for interaction.</p>
 */
@SpringBootApplication
public class IamServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IamServiceApplication.class, args);
    }
}