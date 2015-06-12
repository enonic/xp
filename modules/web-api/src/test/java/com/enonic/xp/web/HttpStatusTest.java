package com.enonic.xp.web;

import org.junit.Test;

import static org.junit.Assert.*;

public class HttpStatusTest
{

    @Test
    public void test100()
    {
        testEnum( HttpStatus.CONTINUE, 100, "Continue" );
    }

    @Test
    public void test101()
    {
        testEnum( HttpStatus.SWITCHING_PROTOCOLS, 101, "Switching Protocols" );
    }

    @Test
    public void test102()
    {
        testEnum( HttpStatus.PROCESSING, 102, "Processing" );
    }

    @Test
    public void test103()
    {
        testEnum( HttpStatus.CHECKPOINT, 103, "Checkpoint" );
    }

    @Test
    public void test200()
    {
        testEnum( HttpStatus.OK, 200, "OK" );
    }

    @Test
    public void test201()
    {
        testEnum( HttpStatus.CREATED, 201, "Created" );
    }

    @Test
    public void test202()
    {
        testEnum( HttpStatus.ACCEPTED, 202, "Accepted" );
    }

    @Test
    public void test203()
    {
        testEnum( HttpStatus.NON_AUTHORITATIVE_INFORMATION, 203, "Non-Authoritative Information" );
    }

    @Test
    public void test204()
    {
        testEnum( HttpStatus.NO_CONTENT, 204, "No Content" );
    }

    @Test
    public void test205()
    {
        testEnum( HttpStatus.RESET_CONTENT, 205, "Reset Content" );
    }

    @Test
    public void test206()
    {
        testEnum( HttpStatus.PARTIAL_CONTENT, 206, "Partial Content" );
    }

    @Test
    public void test207()
    {
        testEnum( HttpStatus.MULTI_STATUS, 207, "Multi-Status" );
    }

    @Test
    public void test208()
    {
        testEnum( HttpStatus.ALREADY_REPORTED, 208, "Already Reported" );
    }

    @Test
    public void test226()
    {
        testEnum( HttpStatus.IM_USED, 226, "IM Used" );
    }

    @Test
    public void test300()
    {
        testEnum( HttpStatus.IM_USED, 226, "IM Used" );
    }

    @Test
    public void test301()
    {
        testEnum( HttpStatus.MOVED_PERMANENTLY, 301, "Moved Permanently" );
    }

    @Test
    public void test302()
    {
        testEnum( HttpStatus.FOUND, 302, "Found" );
    }

    @Test
    public void test302deprecated()
    {
        testEnum( HttpStatus.MOVED_TEMPORARILY, 302, "Moved Temporarily" );
    }

    @Test
    public void test303()
    {
        testEnum( HttpStatus.SEE_OTHER, 303, "See Other" );
    }

    @Test
    public void test304()
    {
        testEnum( HttpStatus.NOT_MODIFIED, 304, "Not Modified" );
    }

    @Test
    public void test305()
    {
        testEnum( HttpStatus.USE_PROXY, 305, "Use Proxy" );
    }

    @Test
    public void test307()
    {
        testEnum( HttpStatus.TEMPORARY_REDIRECT, 307, "Temporary Redirect" );
    }

    @Test
    public void test308()
    {
        testEnum( HttpStatus.PERMANENT_REDIRECT, 308, "Permanent Redirect" );
    }

    @Test
    public void test400()
    {
        testEnum( HttpStatus.BAD_REQUEST, 400, "Bad Request" );
    }

    @Test
    public void test401()
    {
        testEnum( HttpStatus.UNAUTHORIZED, 401, "Unauthorized" );
    }

    @Test
    public void test402()
    {
        testEnum( HttpStatus.PAYMENT_REQUIRED, 402, "Payment Required" );
    }

    @Test
    public void test403()
    {
        testEnum( HttpStatus.FORBIDDEN, 403, "Forbidden" );
    }

    @Test
    public void test404()
    {
        testEnum( HttpStatus.NOT_FOUND, 404, "Not Found" );
    }

    @Test
    public void test405()
    {
        testEnum( HttpStatus.METHOD_NOT_ALLOWED, 405, "Method Not Allowed" );
    }

    @Test
    public void test406()
    {
        testEnum( HttpStatus.NOT_ACCEPTABLE, 406, "Not Acceptable" );
    }

    @Test
    public void test407()
    {
        testEnum( HttpStatus.PROXY_AUTHENTICATION_REQUIRED, 407, "Proxy Authentication Required" );
    }

    @Test
    public void test408()
    {
        testEnum( HttpStatus.REQUEST_TIMEOUT, 408, "Request Timeout" );
    }

    @Test
    public void test409()
    {
        testEnum( HttpStatus.CONFLICT, 409, "Conflict" );
    }

    @Test
    public void test410()
    {
        testEnum( HttpStatus.GONE, 410, "Gone" );
    }

    @Test
    public void test411()
    {
        testEnum( HttpStatus.LENGTH_REQUIRED, 411, "Length Required" );
    }

    @Test
    public void test412()
    {
        testEnum( HttpStatus.PRECONDITION_FAILED, 412, "Precondition Failed" );
    }

    @Test
    public void test413()
    {
        testEnum( HttpStatus.PAYLOAD_TOO_LARGE, 413, "Payload Too Large" );
    }

    @Test
    public void test413deprecated()
    {
        testEnum( HttpStatus.REQUEST_ENTITY_TOO_LARGE, 413, "Request Entity Too Large" );
    }

    @Test
    public void test414()
    {
        testEnum( HttpStatus.URI_TOO_LONG, 414, "URI Too Long" );
    }

    @Test
    public void test414deprecated()
    {
        testEnum( HttpStatus.REQUEST_URI_TOO_LONG, 414, "Request-URI Too Long" );
    }

    @Test
    public void test415()
    {
        testEnum( HttpStatus.UNSUPPORTED_MEDIA_TYPE, 415, "Unsupported Media Type" );
    }

    @Test
    public void test416()
    {
        testEnum( HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, 416, "Requested range not satisfiable" );
    }

    @Test
    public void test417()
    {
        testEnum( HttpStatus.EXPECTATION_FAILED, 417, "Expectation Failed" );
    }

    @Test
    public void test419()
    {
        testEnum( HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE, 419, "Insufficient Space On Resource" );
    }

    @Test
    public void test420()
    {
        testEnum( HttpStatus.METHOD_FAILURE, 420, "Method Failure" );
    }

    @Test
    public void test421()
    {
        testEnum( HttpStatus.DESTINATION_LOCKED, 421, "Destination Locked" );
    }

    @Test
    public void test422()
    {
        testEnum( HttpStatus.UNPROCESSABLE_ENTITY, 422, "Unprocessable Entity" );
    }

    @Test
    public void test423()
    {
        testEnum( HttpStatus.LOCKED, 423, "Locked" );
    }

    @Test
    public void test424()
    {
        testEnum( HttpStatus.FAILED_DEPENDENCY, 424, "Failed Dependency" );
    }

    @Test
    public void test426()
    {
        testEnum( HttpStatus.UPGRADE_REQUIRED, 426, "Upgrade Required" );
    }

    @Test
    public void test428()
    {
        testEnum( HttpStatus.PRECONDITION_REQUIRED, 428, "Precondition Required" );
    }

    @Test
    public void test429()
    {
        testEnum( HttpStatus.TOO_MANY_REQUESTS, 429, "Too Many Requests" );
    }

    @Test
    public void test431()
    {
        testEnum( HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE, 431, "Request Header Fields Too Large" );
    }

    @Test
    public void test500()
    {
        testEnum( HttpStatus.INTERNAL_SERVER_ERROR, 500, "Internal Server Error" );
    }

    @Test
    public void test501()
    {
        testEnum( HttpStatus.NOT_IMPLEMENTED, 501, "Not Implemented" );
    }

    @Test
    public void test502()
    {
        testEnum( HttpStatus.BAD_GATEWAY, 502, "Bad Gateway" );
    }

    @Test
    public void test503()
    {
        testEnum( HttpStatus.SERVICE_UNAVAILABLE, 503, "Service Unavailable" );
    }

    @Test
    public void test504()
    {
        testEnum( HttpStatus.GATEWAY_TIMEOUT, 504, "Gateway Timeout" );
    }

    @Test
    public void test505()
    {
        testEnum( HttpStatus.HTTP_VERSION_NOT_SUPPORTED, 505, "HTTP Version not supported" );
    }

    @Test
    public void test506()
    {
        testEnum( HttpStatus.VARIANT_ALSO_NEGOTIATES, 506, "Variant Also Negotiates" );
    }

    @Test
    public void test507()
    {
        testEnum( HttpStatus.INSUFFICIENT_STORAGE, 507, "Insufficient Storage" );
    }

    @Test
    public void test508()
    {
        testEnum( HttpStatus.LOOP_DETECTED, 508, "Loop Detected" );
    }

    @Test
    public void test509()
    {
        testEnum( HttpStatus.BANDWIDTH_LIMIT_EXCEEDED, 509, "Bandwidth Limit Exceeded" );
    }

    @Test
    public void test510()
    {
        testEnum( HttpStatus.NOT_EXTENDED, 510, "Not Extended" );
    }

    @Test
    public void test511()
    {
        testEnum( HttpStatus.NETWORK_AUTHENTICATION_REQUIRED, 511, "Network Authentication Required" );
    }

    @Test
    public void testInformational()
    {
        assertTrue( HttpStatus.CONTINUE.is1xxInformational() );
        assertFalse( HttpStatus.CONTINUE.is2xxSuccessful() );
    }

    @Test
    public void testSuccessful()
    {
        assertTrue( HttpStatus.OK.is2xxSuccessful() );
        assertFalse( HttpStatus.OK.is1xxInformational() );
    }

    @Test
    public void testRedirection()
    {
        assertTrue( HttpStatus.MOVED_PERMANENTLY.is3xxRedirection() );
        assertFalse( HttpStatus.MOVED_PERMANENTLY.is1xxInformational() );
    }

    @Test
    public void testClientError()
    {
        assertTrue( HttpStatus.URI_TOO_LONG.is4xxClientError() );
        assertFalse( HttpStatus.URI_TOO_LONG.is1xxInformational() );
    }

    @Test
    public void testServerError()
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
