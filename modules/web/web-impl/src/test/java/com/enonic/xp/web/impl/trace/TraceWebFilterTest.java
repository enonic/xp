package com.enonic.xp.web.impl.trace;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.trace.TestTrace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TraceWebFilterTest
{
    private TraceWebFilter filter;

    private WebRequest request;

    private WebResponse response;

    private WebHandlerChain chain;

    @BeforeEach
    void setUp()
    {
        this.filter = new TraceWebFilter();

        this.request = new WebRequest();
        this.request.setMethod( HttpMethod.GET );
        this.request.setPath( "/site/myproject/master/mysite" );
        this.request.setRawPath( "/site/myproject/master/mysite" );
        this.request.setUrl( "http://localhost:8080/site/myproject/master/mysite" );
        this.request.setHost( "localhost" );

        this.response = WebResponse.create().build();
        this.chain = mock( WebHandlerChain.class );
    }

    @Test
    void canHandle()
    {
        assertTrue( this.filter.canHandle( this.request ) );

        this.request.setRawPath( "/somewhere/else" );
        assertFalse( this.filter.canHandle( this.request ) );
    }

    @Test
    void doHandleRecordsTraceAttributes()
        throws Exception
    {
        final WebResponse chainResponse = WebResponse.create().body( "OK" ).build();
        when( this.chain.handle( this.request, this.response ) ).thenReturn( chainResponse );

        // outside OSGi the @Traced wrapper is inert; a manually bound trace exercises the attribute enrichment code
        final TestTrace trace = TestTrace.of( "portalRequest" );
        final WebResponse result = Tracer.traceEx( trace, () -> this.filter.doHandle( this.request, this.response, this.chain ) );

        assertSame( chainResponse, result );
        assertEquals( "/site/myproject/master/mysite", trace.get( "path" ) );
        assertEquals( "/site/myproject/master/mysite", trace.get( "rawpath" ) );
        assertEquals( "http://localhost:8080/site/myproject/master/mysite", trace.get( "url" ) );
        assertEquals( "GET", trace.get( "method" ) );
        assertEquals( "localhost", trace.get( "host" ) );
        assertEquals( 200L, trace.get( "status" ) );
        assertInstanceOf( String.class, trace.get( "type" ) );
        assertInstanceOf( Long.class, trace.get( "size" ) );

        // the default test context has no repository, branch or authenticated user - enrichment must not fail on them
        assertFalse( trace.containsKey( "repo" ) );
        assertFalse( trace.containsKey( "branch" ) );
        assertFalse( trace.containsKey( "user" ) );
    }

    @Test
    void doHandleRecordsStatusFromChainResponse()
        throws Exception
    {
        final WebResponse chainResponse = WebResponse.create().status( HttpStatus.NOT_FOUND ).build();
        when( this.chain.handle( this.request, this.response ) ).thenReturn( chainResponse );

        final TestTrace trace = TestTrace.of( "portalRequest" );
        final WebResponse result = Tracer.traceEx( trace, () -> this.filter.doHandle( this.request, this.response, this.chain ) );

        assertSame( chainResponse, result );
        assertEquals( 404L, trace.get( "status" ) );
    }

    @Test
    void doHandleSkipsUnknownSize()
        throws Exception
    {
        final WebResponse chainResponse = WebResponse.create().body( Map.of( "key", "value" ) ).build();
        when( this.chain.handle( this.request, this.response ) ).thenReturn( chainResponse );

        final TestTrace trace = TestTrace.of( "portalRequest" );
        final WebResponse result = Tracer.traceEx( trace, () -> this.filter.doHandle( this.request, this.response, this.chain ) );

        assertSame( chainResponse, result );
        assertEquals( 200L, trace.get( "status" ) );
        assertFalse( trace.containsKey( "size" ) );
    }

    @Test
    void responseSize()
    {
        assertEquals( 100L, TraceWebFilter.responseSize( WebResponse.create().header( HttpHeaders.CONTENT_LENGTH, "100" ).build() ) );
        assertNull( TraceWebFilter.responseSize( WebResponse.create().header( HttpHeaders.CONTENT_LENGTH, "abc" ).build() ) );
        assertEquals( 4L, TraceWebFilter.responseSize( WebResponse.create().body( "body" ).build() ) );
    }

    @Test
    void bodySize()
        throws Exception
    {
        final Resource resource = mock( Resource.class );
        when( resource.getSize() ).thenReturn( 10L );
        assertEquals( 10L, TraceWebFilter.bodySize( resource, MediaType.PLAIN_TEXT_UTF_8 ) );

        final Resource unknownSizeResource = mock( Resource.class );
        when( unknownSizeResource.getSize() ).thenReturn( -1L );
        assertNull( TraceWebFilter.bodySize( unknownSizeResource, MediaType.PLAIN_TEXT_UTF_8 ) );

        final ByteSource byteSource = mock( ByteSource.class );
        when( byteSource.size() ).thenReturn( 20L );
        assertEquals( 20L, TraceWebFilter.bodySize( byteSource, MediaType.PLAIN_TEXT_UTF_8 ) );

        assertEquals( 10L, TraceWebFilter.bodySize( new byte[10], MediaType.PLAIN_TEXT_UTF_8 ) );
        assertEquals( 0L, TraceWebFilter.bodySize( null, MediaType.PLAIN_TEXT_UTF_8 ) );
        assertNull( TraceWebFilter.bodySize( Map.of( "key", "value" ), MediaType.JSON_UTF_8 ) );
        assertNull( TraceWebFilter.bodySize( List.of( "a", "b" ), MediaType.JSON_UTF_8 ) );
    }

    @Test
    void bodySizeText()
        throws Exception
    {
        assertEquals( 8L, TraceWebFilter.bodySize( "blåbær", MediaType.PLAIN_TEXT_UTF_8 ) );
        assertEquals( 6L, TraceWebFilter.bodySize( "blåbær", MediaType.PLAIN_TEXT_UTF_8.withCharset( StandardCharsets.ISO_8859_1 ) ) );
        assertEquals( 12L, TraceWebFilter.bodySize( "blåbær", MediaType.PLAIN_TEXT_UTF_8.withCharset( StandardCharsets.UTF_16BE ) ) );
        assertNull( TraceWebFilter.bodySize( "blåbær", MediaType.JSON_UTF_8.withoutParameters() ) );

        // unpaired surrogate is replaced when the response is written, so it must not fail the trace
        assertEquals( 1L, TraceWebFilter.bodySize( "\uD800", MediaType.PLAIN_TEXT_UTF_8 ) );
    }
}
