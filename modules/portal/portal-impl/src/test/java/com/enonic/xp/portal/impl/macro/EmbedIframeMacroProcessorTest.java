package com.enonic.xp.portal.impl.macro;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.macro.MacroContext;

import static org.junit.jupiter.api.Assertions.*;

public class EmbedIframeMacroProcessorTest
{
    private EmbedIframeMacroProcessor macroProcessor = new EmbedIframeMacroProcessor();

    @Test
    public void testProcessWithMissingBody()
    {
        assertEquals( "Expected an &lt;iframe&gt; element in Embed macro",
                      macroProcessor.process( makeContext( null, RenderMode.EDIT ) ).getBody() );
    }

    @Test
    public void testProcessNonIframeBody()
    {
        assertEquals( "Expected an &lt;iframe&gt; element in Embed macro",
                      macroProcessor.process( makeContext( "body", RenderMode.EDIT ) ).getBody() );
    }

    @Test
    public void testProcessSelfClosingIframeTagNotAllowed()
    {
        assertEquals( "Expected an &lt;iframe&gt; element in Embed macro",
                      macroProcessor.process( makeContext( "<iframe src=\"www.test.url\"/>", RenderMode.EDIT ) ).getBody() );
    }

    @Test
    public void testProcessNonIframeBodyInLiveMode()
    {
        assertEquals( "", macroProcessor.process( makeContext( "body", RenderMode.LIVE ) ).getBody() );
    }

    @Test
    public void testProcessEscapedIframe()
    {
        assertEquals( "<iframe src=\"www.test.url\"></iframe>", macroProcessor.process(
            makeContext( "&lt;iframe src=\"www.test.url\"&gt;&lt;/iframe&gt;", RenderMode.LIVE ) ).getBody() );
    }

    @Test
    public void testProcessIframe1()
    {
        assertEquals( "<iframe src=\"www.test.url\"></iframe>",
                      macroProcessor.process( makeContext( "<iframe src=\"www.test.url\"></iframe>", RenderMode.EDIT ) ).getBody() );
    }

    @Test
    public void testProcessIframe2()
    {
        assertEquals( "<iframe width='400px' height='400px' src=\"www.test.url\"></iframe>", macroProcessor.process(
            makeContext( "<iframe width='400px' height='400px' src=\"www.test.url\"></iframe>", RenderMode.EDIT ) ).getBody() );
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
