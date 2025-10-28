package upc.edu.muusmart.iamservice.model;

/**
 * Enumeration of application roles used for authorization.
 *
 * <p>Roles in Spring Security are prefixed with "ROLE_" by convention.
 * Defining them as an enum here helps to avoid typos and makes it easier
 * to manage the set of available roles from a single place.</p>
 */
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}