package org.apache.hc.client5.http.utils;

import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TextUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * A collection of utilities for {@link URI URIs}, to workaround
 * bugs within the class or for ease-of-use features.
 *
 * @since 4.0
 */
public class URIUtils {

    /**
     * A convenience method for creating a new {@link URI} whose scheme, host
     * and port are taken from the target host, but whose path, query and
     * fragment are taken from the existing URI. The fragment is only used if
     * dropFragment is false. The path is set to "/" if not explicitly specified.
     *
     * @param uri          Contains the path, query and fragment to use.
     * @param target       Contains the scheme, host and port to use.
     * @param dropFragment True if the fragment should not be copied.
     * @throws URISyntaxException If the resulting URI is invalid.
     */
    public static URI rewriteURI(
            final URI uri,
            final HttpHost target,
            final boolean dropFragment) throws URISyntaxException {
        Args.notNull(uri, "URI");
        if (uri.isOpaque()) {
            return uri;
        }
        final URIBuilder uribuilder = new URIBuilder(uri);
        if (target != null) {
            uribuilder.setScheme(target.getSchemeName());
            uribuilder.setHost(target.getHostName());
            uribuilder.setPort(target.getPort());
        } else {
            uribuilder.setScheme(null);
            uribuilder.setHost((String) null);
            uribuilder.setPort(-1);
        }
        if (dropFragment) {
            uribuilder.setFragment(null);
        }
        final List<String> originalPathSegments = uribuilder.getPathSegments();
        final List<String> pathSegments = new ArrayList<>(originalPathSegments);
        for (final Iterator<String> it = pathSegments.iterator(); it.hasNext(); ) {
            final String pathSegment = it.next();
            if (pathSegment.isEmpty() && it.hasNext()) {
                it.remove();
            }
        }
        if (pathSegments.size() != originalPathSegments.size()) {
            uribuilder.setPathSegments(pathSegments);
        }
        if (pathSegments.isEmpty()) {
            uribuilder.setPathSegments("");
        }
        return uribuilder.build();
    }

    /**
     * A convenience method for
     * {@link URIUtils#rewriteURI(URI, HttpHost, boolean)} that always keeps the
     * fragment.
     */
    public static URI rewriteURI(
            final URI uri,
            final HttpHost target) throws URISyntaxException {
        return rewriteURI(uri, target, false);
    }

    /**
     * A convenience method that creates a new {@link URI} whose scheme, host, port, path,
     * query are taken from the existing URI, dropping any fragment or user-information.
     * The path is set to "/" if not explicitly specified. The existing URI is returned
     * unmodified if it has no fragment or user-information and has a path.
     *
     * @param uri original URI.
     * @throws URISyntaxException If the resulting URI is invalid.
     */
    public static URI rewriteURI(final URI uri) throws URISyntaxException {
        Args.notNull(uri, "URI");
        if (uri.isOpaque()) {
            return uri;
        }
        final URIBuilder uribuilder = new URIBuilder(uri);
        if (uribuilder.getUserInfo() != null) {
            uribuilder.setUserInfo(null);
        }
        if (TextUtils.isEmpty(uribuilder.getPath())) {
            uribuilder.setPath("/");
        }
        if (uribuilder.getHost() != null) {
            uribuilder.setHost(uribuilder.getHost().toLowerCase(Locale.ROOT));
        }
        uribuilder.setFragment(null);
        return uribuilder.build();
    }

    /**
     * Resolves a URI reference against a base URI. Work-around for bug in
     * java.net.URI (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4708535)
     *
     * @param baseURI   the base URI
     * @param reference the URI reference
     * @return the resulting URI
     */
    public static URI resolve(final URI baseURI, final String reference) {
        return resolve(baseURI, URI.create(reference));
    }

    /**
     * Resolves a URI reference against a base URI. Work-around for bugs in
     * java.net.URI (e.g. http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4708535)
     *
     * @param baseURI   the base URI
     * @param reference the URI reference
     * @return the resulting URI
     */
    public static URI resolve(final URI baseURI, final URI reference) {
        Args.notNull(baseURI, "Base URI");
        Args.notNull(reference, "Reference URI");
        final String s = reference.toASCIIString();
        if (s.startsWith("?")) {
            String baseUri = baseURI.toASCIIString();
            final int i = baseUri.indexOf('?');
            baseUri = i > -1 ? baseUri.substring(0, i) : baseUri;
            return URI.create(baseUri + s);
        }
        final boolean emptyReference = s.isEmpty();
        URI resolved;
        if (emptyReference) {
            resolved = baseURI.resolve(URI.create("#"));
            final String resolvedString = resolved.toASCIIString();
            resolved = URI.create(resolvedString.substring(0, resolvedString.indexOf('#')));
        } else {
            resolved = baseURI.resolve(reference);
        }
        try {
            return normalizeSyntax(resolved);
        } catch (final URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Removes dot segments according to RFC 3986, section 5.2.4 and
     * Syntax-Based Normalization according to RFC 3986, section 6.2.2.
     *
     * @param uri the original URI
     * @return the URI without dot segments
     */
    static URI normalizeSyntax(final URI uri) throws URISyntaxException {
        if (uri.isOpaque() || uri.getAuthority() == null) {
            // opaque and file: URIs
            return uri;
        }
        Args.check(uri.isAbsolute(), "Base URI must be absolute");
        final URIBuilder builder = new URIBuilder(uri);
        final String path = builder.getPath();
        if (path != null && !path.equals("/")) {
            final String[] inputSegments = path.split("/");
            final Stack<String> outputSegments = new Stack<>();
            for (final String inputSegment : inputSegments) {
                if ((inputSegment.isEmpty()) || (".".equals(inputSegment))) {
                    // Do nothing
                } else if ("..".equals(inputSegment)) {
                    if (!outputSegments.isEmpty()) {
                        outputSegments.pop();
                    }
                } else {
                    outputSegments.push(inputSegment);
                }
            }
            final StringBuilder outputBuffer = new StringBuilder();
            for (final String outputSegment : outputSegments) {
                outputBuffer.append('/').append(outputSegment);
            }
            if (path.lastIndexOf('/') == path.length() - 1) {
                // path.endsWith("/") || path.equals("")
                outputBuffer.append('/');
            }
            builder.setPath(outputBuffer.toString());
        }
        if (builder.getScheme() != null) {
            builder.setScheme(builder.getScheme().toLowerCase(Locale.ROOT));
        }
        if (builder.getHost() != null) {
            builder.setHost(builder.getHost().toLowerCase(Locale.ROOT));
        }
        return builder.build();
    }

    /**
     * Extracts target host from the given {@link URI}.
     *
     * @param uri
     * @return the target host if the URI is absolute or {@code null} if the URI is
     * relative or does not contain a valid host name.
     * @since 4.1
     */
    public static HttpHost extractHost(final URI uri) {
        if (uri == null) {
            return null;
        }
        HttpHost target = null;
        if (uri.isAbsolute()) {
            int port = uri.getPort(); // may be overridden later
            String host = uri.getHost();
            if (host == null) { // normal parse failed; let's do it ourselves
                // authority does not seem to care about the valid character-set for host names
                host = uri.getAuthority();
                if (host != null) {
                    // Strip off any leading user credentials
                    final int at = host.indexOf('@');
                    if (at >= 0) {
                        if (host.length() > at + 1) {
                            host = host.substring(at + 1);
                        } else {
                            host = null; // @ on its own
                        }
                    }
                    // Extract the port suffix, if present
                    if (host != null) {
                        final int colon = host.indexOf(':');
                        if (colon >= 0) {
                            final int pos = colon + 1;
                            int len = 0;
                            for (int i = pos; i < host.length(); i++) {
                                if (Character.isDigit(host.charAt(i))) {
                                    len++;
                                } else {
                                    break;
                                }
                            }
                            if (len > 0) {
                                try {
                                    port = Integer.parseInt(host.substring(pos, pos + len));
                                } catch (final NumberFormatException ex) {
                                }
                            }
                            host = host.substring(0, colon);
                        }
                    }
                }
            }
            final String scheme = uri.getScheme();
            if (!TextUtils.isBlank(host)) {
                try {
                    target = new HttpHost(scheme, host, port);
                } catch (final IllegalArgumentException ignore) {
                }
            }
        }
        return target;
    }

    /**
     * Derives the interpreted (absolute) URI that was used to generate the last
     * request. This is done by extracting the request-uri and target origin for
     * the last request and scanning all the redirect locations for the last
     * fragment identifier, then combining the result into a {@link URI}.
     *
     * @param originalURI original request before any redirects
     * @param target      if the last URI is relative, it is resolved against this target,
     *                    or {@code null} if not available.
     * @param redirects   collection of redirect locations since the original request
     *                    or {@code null} if not available.
     * @return interpreted (absolute) URI
     */
    public static URI resolve(
            final URI originalURI,
            final HttpHost target,
            final List<URI> redirects) throws URISyntaxException {
        Args.notNull(originalURI, "Request URI");
        final URIBuilder uribuilder;
        if (redirects == null || redirects.isEmpty()) {
            uribuilder = new URIBuilder(originalURI);
        } else {
            uribuilder = new URIBuilder(redirects.get(redirects.size() - 1));
            String frag = uribuilder.getFragment();
            // read interpreted fragment identifier from redirect locations
            for (int i = redirects.size() - 1; frag == null && i >= 0; i--) {
                frag = redirects.get(i).getFragment();
            }
            uribuilder.setFragment(frag);
        }
        // read interpreted fragment identifier from original request
        if (uribuilder.getFragment() == null) {
            uribuilder.setFragment(originalURI.getFragment());
        }
        // last target origin
        if (target != null && !uribuilder.isAbsolute()) {
            uribuilder.setScheme(target.getSchemeName());
            uribuilder.setHost(target.getHostName());
            uribuilder.setPort(target.getPort());
        }
        return uribuilder.build();
    }

    /**
     * Convenience factory method for {@link URI} instances.
     *
     * @since 5.0
     */
    public static URI create(final HttpHost host, final String path) throws URISyntaxException {
        final URIBuilder builder = new URIBuilder(path);
        if (host != null) {
            builder.setHost(host.getHostName()).setPort(host.getPort()).setScheme(host.getSchemeName());
        }
        return builder.build();
    }

    /**
     * Convenience factory method for {@link URI} instances.
     *
     * @since 5.0
     */
    public static URI create(final String scheme, final URIAuthority host, final String path) throws URISyntaxException {
        final URIBuilder builder = new URIBuilder(path);
        if (scheme != null) {
            builder.setScheme(scheme);
        }
        if (host != null) {
            builder.setHost(host.getHostName()).setPort(host.getPort());
        }
        return builder.build();
    }

    /**
     * This class should not be instantiated.
     */
    private URIUtils() {
    }

}
