package com.enonic.xp.portal.impl.postprocess;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.HttpMethod;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PostProcessorImplTest
{
    @Test
    void testPostProcessingInstructions_GET()
        throws Exception
    {
        testPostProcessingInstructions( HttpMethod.GET );
    }

    @Test
    void testPostProcessingInstructions_POST()
        throws Exception
    {
        testPostProcessingInstructions( HttpMethod.POST );
    }

    @Test
    void testPostProcessingInjections_GET()
        throws Exception
    {
        testPostProcessingInjections( HttpMethod.GET );
    }

    @Test
    void testPostProcessingInjections_POST()
        throws Exception
    {
        testPostProcessingInjections( HttpMethod.POST );
    }

    @Test
    void processResponse_skip_non_html()
    {
        final PostProcessorImpl postProcessor = new PostProcessorImpl();

        final PortalResponse portalResponse = PortalResponse.create().contentType( MediaType.JAVASCRIPT_UTF_8 ).body( "" ).build();

        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setMethod( HttpMethod.GET );

        final PortalResponse result = postProcessor.processResponse( portalRequest, portalResponse );
        assertSame( portalResponse, result );
    }

    private void assertEqualsTrimmed( final String expected, final String actual )
    {
        assertEquals( trimLines( expected ), trimLines( actual ) );
    }

    private String trimLines( final String text )
    {
        return text == null ? null : Stream.of( text.split( "\\r?\\n" ) ).map( String::trim ).collect( joining( "\r\n" ) );
    }

    private String readResource( final String resourceName )
        throws Exception
    {
        try (InputStream stream = getClass().getResourceAsStream( resourceName ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }

    private void testPostProcessingInstructions( final HttpMethod httpMethod )
        throws Exception
    {
        final String html = readResource( "postProcessSource1.html" );

        final PostProcessorImpl postProcessor = new PostProcessorImpl();
        postProcessor.addInstruction( new TestPostProcessInstruction() );

        final PortalResponse.Builder portalResponseBuilder = PortalResponse.create().contentType( MediaType.HTML_UTF_8 ).body( html );

        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setMethod( httpMethod );

        final PortalResponse portalResponse = postProcessor.processResponse( portalRequest, portalResponseBuilder.build() );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "postProcessResult1.html" );

        assertEqualsTrimmed( expectedResult, outputHtml );
    }

    private void testPostProcessingInjections( final HttpMethod httpMethod )
        throws Exception
    {
        final String html = readResource( "postProcessSource2.html" );

        final PostProcessorImpl postProcessor = new PostProcessorImpl();
        postProcessor.addInjection( new TestPostProcessInjection() );

        final PortalResponse.Builder portalResponseBuilder = PortalResponse.create().contentType( MediaType.HTML_UTF_8 ).body( html );

        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setMethod( httpMethod );

        final PortalResponse portalResponse = postProcessor.processResponse( portalRequest, portalResponseBuilder.build() );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "postProcessResult2.html" );

        assertEqualsTrimmed( expectedResult, outputHtml );
    }

}
