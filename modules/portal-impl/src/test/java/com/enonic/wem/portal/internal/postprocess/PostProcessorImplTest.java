package com.enonic.wem.portal.internal.postprocess;


import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.controller.PortalRequestImpl;
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

        final PortalRequestImpl req = new PortalRequestImpl();
        req.setMethod( "GET" );

        final PortalContextImpl context = new PortalContextImpl();
        context.setResponse( resp );
        context.setRequest( req );

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

        final PortalRequestImpl req = new PortalRequestImpl();
        req.setMethod( "GET" );

        final PortalContextImpl context = new PortalContextImpl();
        context.setResponse( resp );
        context.setRequest( req );

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
