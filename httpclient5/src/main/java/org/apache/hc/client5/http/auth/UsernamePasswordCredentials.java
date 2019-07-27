package org.apache.hc.client5.http.auth;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.LangUtils;

import java.io.Serializable;
import java.security.Principal;

/**
 * Simple {@link Credentials} representation based on a user name / password
 * pair.
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class UsernamePasswordCredentials implements Credentials, Serializable {

    private static final long serialVersionUID = 243343858802739403L;

    private final BasicUserPrincipal principal;
    private final char[] password;

    /**
     * The constructor with the username and password arguments.
     *
     * @param userName the user name
     * @param password the password
     */
    public UsernamePasswordCredentials(final String userName, final char[] password) {
        super();
        Args.notNull(userName, "Username");
        this.principal = new BasicUserPrincipal(userName);
        this.password = password;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }

    public String getUserName() {
        return this.principal.getName();
    }

    @Override
    public char[] getPassword() {
        return password;
    }

    @Override
    public int hashCode() {
        return this.principal.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UsernamePasswordCredentials) {
            final UsernamePasswordCredentials that = (UsernamePasswordCredentials) o;
            if (LangUtils.equals(this.principal, that.principal)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.principal.toString();
    }

}

