package org.apache.hc.client5.http.auth;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.ietf.jgss.GSSCredential;

import java.io.Serializable;
import java.security.Principal;

/**
 * Kerberos specific {@link Credentials} representation based on {@link GSSCredential}.
 *
 * @since 4.4
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class KerberosCredentials implements Credentials, Serializable {

    private static final long serialVersionUID = 487421613855550713L;

    /**
     * GSSCredential
     */
    private final GSSCredential gssCredential;

    /**
     * Constructor with GSSCredential argument
     *
     * @param gssCredential
     */
    public KerberosCredentials(final GSSCredential gssCredential) {
        this.gssCredential = gssCredential;
    }

    public GSSCredential getGSSCredential() {
        return gssCredential;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public char[] getPassword() {
        return null;
    }

}
