package com.enonic.xp.portal.owasp.impl;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Resources;

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

    private void assertHtml( final String name, final String html )
        throws Exception
    {
        final String resource = readHtml( name + "_sanitized" );
        Assert.assertEquals( resource, html );
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
        return Resources.toString( getClass().getResource( resourceName ), StandardCharsets.UTF_8 );
    }
}