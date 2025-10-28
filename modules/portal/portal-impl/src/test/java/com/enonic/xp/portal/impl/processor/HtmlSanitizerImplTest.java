package com.enonic.xp.portal.impl.processor;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlSanitizerImplTest
{

    @Test
    void testSanitizeHtmlOnClick()
        throws Exception
    {
        final String html = readHtml( "a_onclick" );
        final String sanitized = new HtmlSanitizerImpl().sanitizeHtml( html );
        final String sanitizedFunction = new HtmlSanitizerImpl().sanitizeHtml( html);

        assertHtml( "a_onclick", sanitized );
        assertHtml( "a_onclick", sanitizedFunction );
    }

    @Test
    void testSanitizeHtmlScriptTags()
        throws Exception
    {
        final String html = readHtml( "scripts" );
        final String sanitized = new HtmlSanitizerImpl().sanitizeHtml( html );
        final String sanitizedFunction = new HtmlSanitizerImpl().sanitizeHtml( html);

        assertHtml( "scripts", sanitized );
        assertHtml( "scripts", sanitizedFunction );
    }

    @Test
    void testSanitizeHtmlImgOnMouseOver()
        throws Exception
    {
        final String html = readHtml( "img_mouseover" );
        final String sanitized = new HtmlSanitizerImpl().sanitizeHtml( html );
        final String sanitizedFunction = new HtmlSanitizerImpl().sanitizeHtml( html );

        assertHtml( "img_mouseover", sanitized );
        assertHtml( "img_mouseover", sanitizedFunction );
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
        try (InputStream stream = getClass().getResourceAsStream( resourceName ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }
}
