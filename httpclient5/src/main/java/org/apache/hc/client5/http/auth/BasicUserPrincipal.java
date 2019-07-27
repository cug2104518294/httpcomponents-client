package org.apache.hc.client5.http.auth;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.LangUtils;

import java.io.Serializable;
import java.security.Principal;

/**
 * Basic username based principal representation.
 *
 * @since 4.0
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class BasicUserPrincipal implements Principal, Serializable {

    private static final long serialVersionUID = -2266305184969850467L;

    private final String username;

    public BasicUserPrincipal(final String username) {
        super();
        Args.notNull(username, "User name");
        this.username = username;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public int hashCode() {
        int hash = LangUtils.HASH_SEED;
        hash = LangUtils.hashCode(hash, this.username);
        return hash;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BasicUserPrincipal) {
            final BasicUserPrincipal that = (BasicUserPrincipal) o;
            if (LangUtils.equals(this.username, that.username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[principal: ");
        buffer.append(this.username);
        buffer.append("]");
        return buffer.toString();
    }

}

