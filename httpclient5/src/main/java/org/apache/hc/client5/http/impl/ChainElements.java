package org.apache.hc.client5.http.impl;

/**
 * Supported elements of request execution pipeline.
 *
 * @since 5.0
 */
public enum ChainElements {

    REDIRECT, BACK_OFF, RETRY_SERVICE_UNAVAILABLE, RETRY_IO_ERROR, CACHING, PROTOCOL, CONNECT, MAIN_TRANSPORT

}