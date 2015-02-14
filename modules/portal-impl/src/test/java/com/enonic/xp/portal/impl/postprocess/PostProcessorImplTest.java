package com.enonic.xp.portal.impl.postprocess;


import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalResponse;

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

        assertEquals( expectedResult, outputHtml );
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

        assertEquals( expectedResult, outputHtml );
    }

    private String readResource( final String resourceName )
        throws Exception
    {
        return Resources.toString( getClass().getResource( resourceName ), Charsets.UTF_8 );
    }
}
