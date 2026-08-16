package com.enonic.xp.admin.event.impl;

import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminEventClientTest
{
    private final AdminEventClient client = new AdminEventClient();

    @Test
    void servesTheScript()
    {
        final WebResponse response = client.handle( new WebRequest() );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( MediaType.JAVASCRIPT_UTF_8, response.getContentType() );

        final String script = ( (Resource) response.getBody() ).readString();
        assertTrue( script.contains( "AdminEventsSocket" ) );
        assertTrue( script.contains( "export function connect" ) );
    }

    @Test
    void theScriptIsRevalidatedRatherThanCached()
    {
        final WebResponse response = client.handle( new WebRequest() );

        assertEquals( "no-cache", response.getHeaders().get( HttpHeaders.CACHE_CONTROL ) );
        final String etag = response.getHeaders().get( HttpHeaders.ETAG );
        assertFalse( etag.isEmpty() );
        assertTrue( etag.startsWith( "\"" ) && etag.endsWith( "\"" ) );
    }

    @Test
    void anUnchangedScriptIsNotSentAgain()
    {
        final String etag = client.handle( new WebRequest() ).getHeaders().get( HttpHeaders.ETAG );

        final WebRequest request = new WebRequest();
        request.getHeaders().put( HttpHeaders.IF_NONE_MATCH, etag );

        final WebResponse response = client.handle( request );

        assertEquals( HttpStatus.NOT_MODIFIED, response.getStatus() );
        assertNull( response.getBody() );
        assertEquals( etag, response.getHeaders().get( HttpHeaders.ETAG ) );
    }

    @Test
    void aDifferentEntityTagIsSentTheScript()
    {
        final WebRequest request = new WebRequest();
        request.getHeaders().put( HttpHeaders.IF_NONE_MATCH, "\"stale\"" );

        assertEquals( HttpStatus.OK, client.handle( request ).getStatus() );
    }
}
