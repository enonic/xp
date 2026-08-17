package com.enonic.xp.admin.event.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminEventClientTest
{
    private static final ResourceKey KEY = ResourceKey.from( ApplicationKey.SYSTEM, "/admin/event/client.js" );

    private ResourceService resourceService;

    private AdminEventClient client;

    @BeforeEach
    void setUp()
    {
        final Resource resource = new UrlResource( KEY, AdminEventClientTest.class.getResource( "/admin/event/client.js" ) );

        resourceService = mock( ResourceService.class );
        when( resourceService.getResource( KEY ) ).thenReturn( resource );
        when( resourceService.processResource( any() ) ).thenAnswer( invocation -> {
            final ResourceProcessor<?, ?> processor = invocation.getArgument( 0 );
            return processor.process( resource );
        } );

        client = new AdminEventClient( resourceService );
    }

    @Test
    void servesTheScript()
    {
        final WebResponse response = client.handle( new WebRequest() );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( MediaType.JAVASCRIPT_UTF_8, response.getContentType() );
        assertTrue( ( (Resource) response.getBody() ).readString().contains( "export function connect" ) );
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

    @Test
    void aMissingScriptIsNotFound()
    {
        // doReturn: re-stubbing with when() would run the answer already registered
        doReturn( null ).when( resourceService ).processResource( any() );

        assertEquals( HttpStatus.NOT_FOUND, client.handle( new WebRequest() ).getStatus() );
    }
}
