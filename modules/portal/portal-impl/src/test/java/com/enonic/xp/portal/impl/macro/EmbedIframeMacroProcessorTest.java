package com.enonic.xp.portal.impl.macro;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.macro.MacroContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmbedIframeMacroProcessorTest
{
    private final EmbedIframeMacroProcessor macroProcessor = new EmbedIframeMacroProcessor();

    @Test
    void testProcessWithMissingBody()
    {
        assertEquals( "Expected an &lt;iframe&gt; element in Embed macro",
                      macroProcessor.process( makeContext( "", RenderMode.EDIT ) ).getBody() );
        assertEquals( "Expected an &lt;iframe&gt; element in Embed macro",
                      macroProcessor.process( makeContext( null, RenderMode.EDIT ) ).getBody() );
    }

    @Test
    void testProcessNonIframeBody()
    {
        assertEquals( "Expected an &lt;iframe&gt; element in Embed macro",
                      macroProcessor.process( makeContext( "body", RenderMode.EDIT ) ).getBody() );
    }

    @Test
    void testProcessSelfClosingIframeTagNotAllowed()
    {
        assertEquals( "Expected an &lt;iframe&gt; element in Embed macro",
                      macroProcessor.process( makeContext( "&lt;iframe src=\"www.test.url\"/&gt;", RenderMode.EDIT ) ).getBody() );
    }

    @Test
    void testProcessNonIframeBodyInLiveMode()
    {
        assertEquals( "", macroProcessor.process( makeContext( "body", RenderMode.LIVE ) ).getBody() );
    }

    @Test
    void testProcessEscapedIframe()
    {
        assertEquals( "<iframe src=\"www.test.url\"></iframe>", macroProcessor.process(
            makeContext( "&lt;iframe src=\"www.test.url\"&gt;&lt;/iframe&gt;", RenderMode.LIVE ) ).getBody() );
    }

    @Test
    void testProcessUnscapedIframe()
    {
        assertEquals( "", macroProcessor.process( makeContext( "<iframe src=\"www.test.url\"></iframe>", RenderMode.LIVE ) ).getBody() );
        assertEquals( "Expected an &lt;iframe&gt; element in Embed macro",
                      macroProcessor.process( makeContext( "<iframe src=\"www.test.url\"></iframe>", RenderMode.EDIT ) ).getBody() );
    }

    @Test
    void testProcessIframe1()
    {
        assertEquals( "<iframe src=\"www.test.url\"></iframe>", macroProcessor.process(
            makeContext( "&lt;iframe src=\"www.test.url\"&gt;&lt;/iframe&gt;", RenderMode.EDIT ) ).getBody() );
    }

    @Test
    void testProcessIframe2()
    {
        assertEquals( "<iframe width='400px' height='400px' src=\"www.test.url\"></iframe>", macroProcessor.process(
            makeContext( "&lt;iframe width='400px' height='400px' src=\"www.test.url\"&gt;&lt;/iframe&gt;", RenderMode.EDIT ) ).getBody() );
    }

    private MacroContext makeContext( final String body, final RenderMode renderMode )
    {
        final PortalRequest request = new PortalRequest();
        request.setMode( renderMode );
        return MacroContext.create().name( "name" ).
            request( request ).
            body( body ).
            build();
    }
}
