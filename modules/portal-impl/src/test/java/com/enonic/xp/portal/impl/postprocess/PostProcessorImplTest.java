package com.enonic.xp.portal.impl.postprocess;


import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.portal.PortalContext;
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

        final PortalResponse resp = new PortalResponse();
        resp.setPostProcess( true );
        resp.setBody( html );

        final PortalContext context = new PortalContext();
        context.setResponse( resp );
        context.setMethod( "GET" );

        postProcessor.processResponse( context );

        final String outputHtml = resp.getBody().toString();
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

        final PortalResponse resp = new PortalResponse();
        resp.setPostProcess( true );
        resp.setBody( html );

        final PortalContext context = new PortalContext();
        context.setResponse( resp );
        context.setMethod( "GET" );

        postProcessor.processResponse( context );

        final String outputHtml = resp.getBody().toString();
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
