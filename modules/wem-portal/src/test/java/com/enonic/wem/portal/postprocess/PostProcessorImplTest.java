package com.enonic.wem.portal.postprocess;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;

import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpResponse;

import static junit.framework.Assert.assertEquals;

public class PostProcessorImplTest
{
    @Test
    public void testPostProcessing()
        throws Exception
    {
        final String html = readResource( "postProcessSource.html" );

        final PostProcessorImpl postProcessor = new PostProcessorImpl();
        postProcessor.instructions = Sets.newHashSet();
        postProcessor.instructions.add( new TestPostProcessInstruction() );

        final JsHttpResponse resp = new JsHttpResponse();
        resp.setPostProcess( true );
        resp.setBody( html );

        final JsContext context = new JsContext();
        context.setResponse( resp );

        postProcessor.processResponse( context );

        final String outputHtml = resp.getBody().toString();
        final String expectedResult = readResource( "postProcessResult.html" );

        equalToIgnoringWhiteSpace( expectedResult, outputHtml );
    }

    public String readResource( final String resourceName )
        throws IOException
    {
        try
        {
            final byte[] bytes = Files.readAllBytes( new File( PostProcessorImplTest.class.getResource( resourceName ).toURI() ).toPath() );
            return new String( bytes, Charsets.UTF_8 );
        }
        catch ( URISyntaxException e )
        {
            throw new IllegalStateException( e );
        }
    }

    private void equalToIgnoringWhiteSpace( final String expected, final String actual )
    {
        final String expectedWithoutSpaces = expected.replaceAll( "\\s+", "" );
        final String actualWithoutSpaces = actual.replaceAll( "\\s+", "" );
        if ( !expectedWithoutSpaces.equals( actualWithoutSpaces ) )
        {
            assertEquals( expected, actual );
        }
    }
}
