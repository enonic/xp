package com.enonic.wem.portal.internal.postprocess;


import java.util.List;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.controller.JsHttpResponse;
import com.enonic.wem.portal.postprocess.PostProcessInjection;
import com.enonic.wem.portal.postprocess.PostProcessInstruction;

import static junit.framework.Assert.assertEquals;

public class PostProcessorImplTest
{
    @Test
    public void testPostProcessingInstructions()
        throws Exception
    {
        final String html = readResource( "postProcessSource1.html" );

        final PostProcessorImpl postProcessor = new PostProcessorImpl();

        final List<PostProcessInstruction> instructions = Lists.newArrayList();
        instructions.add( new TestPostProcessInstruction() );

        postProcessor.setInstructions( instructions );
        postProcessor.setInjections( Lists.newArrayList() );

        final JsHttpResponse resp = new JsHttpResponse();
        resp.setPostProcess( true );
        resp.setBody( html );

        final JsContext context = new JsContext();
        context.setResponse( resp );

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

        final List<PostProcessInjection> injections = Lists.newArrayList();
        injections.add( new TestPostProcessInjection() );

        postProcessor.setInjections( injections );
        postProcessor.setInstructions( Lists.newArrayList() );

        final JsHttpResponse resp = new JsHttpResponse();
        resp.setPostProcess( true );
        resp.setBody( html );

        final JsContext context = new JsContext();
        context.setResponse( resp );

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
