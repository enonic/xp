package com.enonic.xp.portal.impl.macro;

import org.junit.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.macro.MacroContext;

import static org.junit.Assert.*;

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
    public void testProcessNonIframeBodyInLiveMode()
    {
        assertEquals( "", macroProcessor.process( makeContext( "body", RenderMode.LIVE ) ).getBody() );
    }

    @Test
    public void testProcessIframeBody()
    {
        assertEquals( "<iframe src=\"data:text/html;charset=utf-8,test\"></iframe>",
                      macroProcessor.process( makeContext( "<iframe>test</iframe>", RenderMode.EDIT ) ).getBody() );
    }

    @Test
    public void testProcessIframeBodyWithScript()
    {
        assertEquals(
            "<iframe src=\"data:text/html;charset=utf-8,<button type='button' onclick=&quot;document.getElementById('demo').innerHTML = Date()" +
                "&quot;>Click me to display Date and Time.</button><p id=&quot;demo&quot;></p>\"></iframe>", macroProcessor.process(
                makeContext( "<iframe><button type='button' onclick=\"document.getElementById('demo').innerHTML = Date()\">" +
                                 "Click me to display Date and Time.</button><p id=\"demo\"></p></iframe>", RenderMode.EDIT ) ).getBody() );
    }

    @Test
    public void testProcessIframeBodyWithMultilineScript()
    {
        assertEquals(
            "<iframe src=\"data:text/html;charset=utf-8,<button type='button' onclick=&quot;document.getElementById('demo').innerHTML = Date() \n" +
                "&quot;>Click me to display Date and Time.</button><p id=&quot;demo&quot;></p>\"></iframe>", macroProcessor.process(
                makeContext( "<iframe><button type='button' onclick=\"document.getElementById('demo').innerHTML = Date() \n\">" +
                                 "Click me to display Date and Time.</button><p id=\"demo\"></p></iframe>", RenderMode.EDIT ) ).getBody() );
    }

    @Test
    public void testProcessEscapedIframe()
    {
        assertEquals( "<iframe src=\"data:text/html;charset=utf-8,test\"></iframe>",
                      macroProcessor.process( makeContext( "&lt;iframe&gt;test&lt;/iframe&gt;", RenderMode.EDIT ) ).getBody() );
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
