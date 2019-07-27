package org.apache.hc.client5.http.auth;

import java.security.Principal;

/**
 * This interface represents a set of credentials consisting of a security
 * principal and a secret (password) that can be used to establish user
 * identity
 *
 * @since 4.0
 */
public interface Credentials {

    Principal getUserPrincipal();

    char[] getPassword();

}
