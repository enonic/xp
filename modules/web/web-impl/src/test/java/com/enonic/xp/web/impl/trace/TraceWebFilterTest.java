package com.enonic.xp.web.impl.trace;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
