package com.enonic.xp.portal.impl.postprocess;


import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

import static java.util.stream.Collectors.joining;
import static junit.framework.Assert.assertEquals;

public class PostProcessorImplTest
{
    @Test
    public void testPostProcessingInstructions()
        throws Exception
    {
        final String html = readResource( "postProcessSource1.html" );

        final PostProcessorImpl postProcessor = new PostProcessorImpl();
        postProcessor.addInstruction( new TestPostProcessInstruction() );

        final PortalResponse.Builder portalResponseBuilder = PortalResponse.create().postProcess( true ).body( html );

        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMethod( "GET" );

        final PortalResponse portalResponse = postProcessor.processResponse( portalRequest, portalResponseBuilder.build() );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "postProcessResult1.html" );

        assertEqualsTrimmed( expectedResult, outputHtml );
    }

    @Test
    public void testPostProcessingInjections()
        throws Exception
    {
        final String html = readResource( "postProcessSource2.html" );

        final PostProcessorImpl postProcessor = new PostProcessorImpl();
        postProcessor.addInjection( new TestPostProcessInjection() );

        final PortalResponse.Builder portalResponseBuilder = PortalResponse.create().postProcess( true ).body( html );

        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMethod( "GET" );

        final PortalResponse portalResponse = postProcessor.processResponse( portalRequest, portalResponseBuilder.build() );

        final String outputHtml = portalResponse.getBody().toString();
        final String expectedResult = readResource( "postProcessResult2.html" );

        assertEqualsTrimmed( expectedResult, outputHtml );
    }

    private void assertEqualsTrimmed( final String expected, final String actual )
    {
        assertEquals( trimLines( expected ), trimLines( actual ) );
    }

    private String trimLines( final String text )
    {
        return text == null ? null : Stream.of( text.split( "\\r?\\n" ) ).
            map( String::trim ).
            collect( joining( "\r\n" ) );
    }

    private String readResource( final String resourceName )
        throws Exception
    {
        return Resources.toString( getClass().getResource( resourceName ), Charsets.UTF_8 );
    }
}
