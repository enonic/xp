package com.enonic.xp.web.impl.serializer;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebSerializerServiceImplTest
{
    @Test
    void request_doesNotReadBody()
        throws Exception
    {
        final HttpServletRequest req = mockJsonRequest( "{\"key\":\"value\"}" );

        final WebRequest webRequest = new WebSerializerServiceImpl().request( req );

        assertNull( webRequest.getBodyAsString() );
        assertEquals( "application/json", webRequest.getContentType() );
        assertEquals( HttpMethod.POST, webRequest.getMethod() );
        verify( req, never() ).getReader();
    }

    @Test
    void serializeBody_readsJsonBody()
        throws Exception
    {
        final String body = "{\"key\":\"value\"}";
        final HttpServletRequest req = mockJsonRequest( body );

        final WebSerializerServiceImpl service = new WebSerializerServiceImpl();
        final WebRequest webRequest = service.request( req );
        service.serializeBody( webRequest, req );

        assertEquals( body, webRequest.getBodyAsString() );
    }

    private static HttpServletRequest mockJsonRequest( final String body )
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getMethod() ).thenReturn( "POST" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getServerPort() ).thenReturn( 8080 );
        when( req.getRequestURI() ).thenReturn( "/site" );
        when( req.getPathInfo() ).thenReturn( "/site" );
        when( req.getContentType() ).thenReturn( "application/json" );
        when( req.getLocales() ).thenReturn( Collections.enumeration( List.of( Locale.ENGLISH ) ) );
        when( req.getHeaderNames() ).thenReturn( Collections.emptyEnumeration() );
        when( req.getReader() ).thenReturn( new BufferedReader( new StringReader( body ) ) );
        return req;
    }
}
