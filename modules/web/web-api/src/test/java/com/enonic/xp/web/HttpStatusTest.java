package com.enonic.xp.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpStatusTest
{

    @Test
    void test100()
    {
        testEnum( HttpStatus.CONTINUE, 100, "Continue" );
    }

    @Test
    void test101()
    {
        testEnum( HttpStatus.SWITCHING_PROTOCOLS, 101, "Switching Protocols" );
    }

    @Test
    void test102()
    {
        testEnum( HttpStatus.PROCESSING, 102, "Processing" );
    }

    @Test
    void test103()
    {
        testEnum( HttpStatus.CHECKPOINT, 103, "Checkpoint" );
    }

    @Test
    void test200()
    {
        testEnum( HttpStatus.OK, 200, "OK" );
    }

    @Test
    void test201()
    {
        testEnum( HttpStatus.CREATED, 201, "Created" );
    }

    @Test
    void test202()
    {
        testEnum( HttpStatus.ACCEPTED, 202, "Accepted" );
    }

    @Test
    void test203()
    {
        testEnum( HttpStatus.NON_AUTHORITATIVE_INFORMATION, 203, "Non-Authoritative Information" );
    }

    @Test
    void test204()
    {
        testEnum( HttpStatus.NO_CONTENT, 204, "No Content" );
    }

    @Test
    void test205()
    {
        testEnum( HttpStatus.RESET_CONTENT, 205, "Reset Content" );
    }

    @Test
    void test206()
    {
        testEnum( HttpStatus.PARTIAL_CONTENT, 206, "Partial Content" );
    }

    @Test
    void test207()
    {
        testEnum( HttpStatus.MULTI_STATUS, 207, "Multi-Status" );
    }

    @Test
    void test208()
    {
        testEnum( HttpStatus.ALREADY_REPORTED, 208, "Already Reported" );
    }

    @Test
    void test226()
    {
        testEnum( HttpStatus.IM_USED, 226, "IM Used" );
    }

    @Test
    void test300()
    {
        testEnum( HttpStatus.IM_USED, 226, "IM Used" );
    }

    @Test
    void test301()
    {
        testEnum( HttpStatus.MOVED_PERMANENTLY, 301, "Moved Permanently" );
    }

    @Test
    void test302()
    {
        testEnum( HttpStatus.FOUND, 302, "Found" );
    }

    @Test
    void test303()
    {
        testEnum( HttpStatus.SEE_OTHER, 303, "See Other" );
    }

    @Test
    void test304()
    {
        testEnum( HttpStatus.NOT_MODIFIED, 304, "Not Modified" );
    }

    @Test
    void test307()
    {
        testEnum( HttpStatus.TEMPORARY_REDIRECT, 307, "Temporary Redirect" );
    }

    @Test
    void test308()
    {
        testEnum( HttpStatus.PERMANENT_REDIRECT, 308, "Permanent Redirect" );
    }

    @Test
    void test400()
    {
        testEnum( HttpStatus.BAD_REQUEST, 400, "Bad Request" );
    }

    @Test
    void test401()
    {
        testEnum( HttpStatus.UNAUTHORIZED, 401, "Unauthorized" );
    }

    @Test
    void test402()
    {
        testEnum( HttpStatus.PAYMENT_REQUIRED, 402, "Payment Required" );
    }

    @Test
    void test403()
    {
        testEnum( HttpStatus.FORBIDDEN, 403, "Forbidden" );
    }

    @Test
    void test404()
    {
        testEnum( HttpStatus.NOT_FOUND, 404, "Not Found" );
    }

    @Test
    void test405()
    {
        testEnum( HttpStatus.METHOD_NOT_ALLOWED, 405, "Method Not Allowed" );
    }

    @Test
    void test406()
    {
        testEnum( HttpStatus.NOT_ACCEPTABLE, 406, "Not Acceptable" );
    }

    @Test
    void test407()
    {
        testEnum( HttpStatus.PROXY_AUTHENTICATION_REQUIRED, 407, "Proxy Authentication Required" );
    }

    @Test
    void test408()
    {
        testEnum( HttpStatus.REQUEST_TIMEOUT, 408, "Request Timeout" );
    }

    @Test
    void test409()
    {
        testEnum( HttpStatus.CONFLICT, 409, "Conflict" );
    }

    @Test
    void test410()
    {
        testEnum( HttpStatus.GONE, 410, "Gone" );
    }

    @Test
    void test411()
    {
        testEnum( HttpStatus.LENGTH_REQUIRED, 411, "Length Required" );
    }

    @Test
    void test412()
    {
        testEnum( HttpStatus.PRECONDITION_FAILED, 412, "Precondition Failed" );
    }

    @Test
    void test413()
    {
        testEnum( HttpStatus.PAYLOAD_TOO_LARGE, 413, "Payload Too Large" );
    }

    @Test
    void test414()
    {
        testEnum( HttpStatus.URI_TOO_LONG, 414, "URI Too Long" );
    }

    @Test
    void test415()
    {
        testEnum( HttpStatus.UNSUPPORTED_MEDIA_TYPE, 415, "Unsupported Media Type" );
    }

    @Test
    void test416()
    {
        testEnum( HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, 416, "Requested range not satisfiable" );
    }

    @Test
    void test417()
    {
        testEnum( HttpStatus.EXPECTATION_FAILED, 417, "Expectation Failed" );
    }

    @Test
    void test422()
    {
        testEnum( HttpStatus.UNPROCESSABLE_ENTITY, 422, "Unprocessable Entity" );
    }

    @Test
    void test423()
    {
        testEnum( HttpStatus.LOCKED, 423, "Locked" );
    }

    @Test
    void test424()
    {
        testEnum( HttpStatus.FAILED_DEPENDENCY, 424, "Failed Dependency" );
    }

    @Test
    void test426()
    {
        testEnum( HttpStatus.UPGRADE_REQUIRED, 426, "Upgrade Required" );
    }

    @Test
    void test428()
    {
        testEnum( HttpStatus.PRECONDITION_REQUIRED, 428, "Precondition Required" );
    }

    @Test
    void test429()
    {
        testEnum( HttpStatus.TOO_MANY_REQUESTS, 429, "Too Many Requests" );
    }

    @Test
    void test431()
    {
        testEnum( HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE, 431, "Request Header Fields Too Large" );
    }

    @Test
    void test500()
    {
        testEnum( HttpStatus.INTERNAL_SERVER_ERROR, 500, "Internal Server Error" );
    }

    @Test
    void test501()
    {
        testEnum( HttpStatus.NOT_IMPLEMENTED, 501, "Not Implemented" );
    }

    @Test
    void test502()
    {
        testEnum( HttpStatus.BAD_GATEWAY, 502, "Bad Gateway" );
    }

    @Test
    void test503()
    {
        testEnum( HttpStatus.SERVICE_UNAVAILABLE, 503, "Service Unavailable" );
    }

    @Test
    void test504()
    {
        testEnum( HttpStatus.GATEWAY_TIMEOUT, 504, "Gateway Timeout" );
    }

    @Test
    void test505()
    {
        testEnum( HttpStatus.HTTP_VERSION_NOT_SUPPORTED, 505, "HTTP Version not supported" );
    }

    @Test
    void test506()
    {
        testEnum( HttpStatus.VARIANT_ALSO_NEGOTIATES, 506, "Variant Also Negotiates" );
    }

    @Test
    void test507()
    {
        testEnum( HttpStatus.INSUFFICIENT_STORAGE, 507, "Insufficient Storage" );
    }

    @Test
    void test508()
    {
        testEnum( HttpStatus.LOOP_DETECTED, 508, "Loop Detected" );
    }

    @Test
    void test509()
    {
        testEnum( HttpStatus.BANDWIDTH_LIMIT_EXCEEDED, 509, "Bandwidth Limit Exceeded" );
    }

    @Test
    void test510()
    {
        testEnum( HttpStatus.NOT_EXTENDED, 510, "Not Extended" );
    }

    @Test
    void test511()
    {
        testEnum( HttpStatus.NETWORK_AUTHENTICATION_REQUIRED, 511, "Network Authentication Required" );
    }

    @Test
    void testInformational()
    {
        assertTrue( HttpStatus.CONTINUE.is1xxInformational() );
        assertFalse( HttpStatus.CONTINUE.is2xxSuccessful() );
    }

    @Test
    void testSuccessful()
    {
        assertTrue( HttpStatus.OK.is2xxSuccessful() );
        assertFalse( HttpStatus.OK.is1xxInformational() );
    }

    @Test
    void testRedirection()
    {
        assertTrue( HttpStatus.MOVED_PERMANENTLY.is3xxRedirection() );
        assertFalse( HttpStatus.MOVED_PERMANENTLY.is1xxInformational() );
    }

    @Test
    void testClientError()
    {
        assertTrue( HttpStatus.URI_TOO_LONG.is4xxClientError() );
        assertFalse( HttpStatus.URI_TOO_LONG.is1xxInformational() );
    }

    @Test
    void testServerError()
    {
        assertTrue( HttpStatus.GATEWAY_TIMEOUT.is5xxServerError() );
        assertFalse( HttpStatus.GATEWAY_TIMEOUT.is1xxInformational() );
    }

    private void testEnum( final HttpStatus status, final int value, final String reason )
    {
        assertEquals( value, status.value() );
        assertEquals( reason, status.getReasonPhrase() );
        assertEquals( String.valueOf( value ), status.toString() );
    }
}
