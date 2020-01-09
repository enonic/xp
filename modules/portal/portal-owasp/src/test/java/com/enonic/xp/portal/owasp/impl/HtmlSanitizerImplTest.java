package com.enonic.xp.portal.owasp.impl;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlSanitizerImplTest
{

    @Test
    public void testSanitizeHtmlOnClick()
        throws Exception
    {
        final String html = readHtml( "a_onclick" );
        final String sanitized = new HtmlSanitizerImpl().sanitizeHtml( html );

        assertHtml( "a_onclick", sanitized );
    }

    @Test
    public void testSanitizeHtmlScriptTags()
        throws Exception
    {
        final String html = readHtml( "scripts" );
        final String sanitized = new HtmlSanitizerImpl().sanitizeHtml( html );

        assertHtml( "scripts", sanitized );
    }

    @Test
    public void testSanitizeHtmlImgOnMouseOver()
        throws Exception
    {
        final String html = readHtml( "img_mouseover" );
        final String sanitized = new HtmlSanitizerImpl().sanitizeHtml( html );

        assertHtml( "img_mouseover", sanitized );
    }

    @Test
    public void testSanitizeHtmlImgOnError()
        throws Exception
    {
        final String html = readHtml( "img_onerror" );
        final String sanitized = new HtmlSanitizerImpl().sanitizeHtml( html );

        assertHtml( "img_onerror", sanitized );
    }

    @Test
    public void testSanitizeHtmlIframe()
        throws Exception
    {
        final String html = readHtml( "iframe" );
        final String sanitized = new HtmlSanitizerImpl().sanitizeHtml( html );

        assertHtml( "iframe", sanitized );
    }

    @Test
    public void testSanitizeHtmlTable()
        throws Exception
    {
        final String html = readHtml( "table" );
        final String sanitized = new HtmlSanitizerImpl().sanitizeHtml( html );

        assertHtml( "table", sanitized );
    }

    @Test
    public void testSanitizeHtmlBlockTags()
        throws Exception
    {
        final String html = readHtml( "block" );
        final String sanitized = new HtmlSanitizerImpl().sanitizeHtml( html );

        assertHtml( "block", sanitized );
    }

    @Test
    public void testSanitizeHtmlInlineTags()
        throws Exception
    {
        final String html = readHtml( "inline" );
        final String sanitized = new HtmlSanitizerImpl().sanitizeHtml( html );

        assertHtml( "inline", sanitized );
    }

    @Test
    public void testSanitizeHtmlFigureTags()
        throws Exception
    {
        final String html = readHtml( "figure_tag" );
        final String sanitized = new HtmlSanitizerImpl().sanitizeHtml( html );

        assertHtml( "figure_tag", sanitized );
    }

    private void assertHtml( final String name, final String html )
        throws Exception
    {
        final String resource = readHtml( name + "_sanitized" );
        assertEquals( resource, html );
    }

    private String readHtml( final String name )
        throws Exception
    {
        final String resourceName = "/" + getClass().getPackage().getName().replace( '.', '/' ) + "/" + name + ".html";
        return readResource( resourceName );
    }

    private String readResource( final String resourceName )
        throws Exception
    {
        try (final InputStream stream = getClass().getResourceAsStream( resourceName ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }
}
