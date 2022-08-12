package com.enonic.xp.portal.owasp.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlRichTextSanitizerTest
{

    @Test
    public void testSanitizeImg()
        throws Exception
    {
        final String html = "<img srcset='srcSetValue' alt='altValue' custom='customValue' src='sourceValue' id='idValue'/>";
        final String sanitized = new HtmlRichTextSanitizer().sanitize( html );

        assertEquals( "<img alt=\"altValue\" src=\"sourceValue\" />", sanitized );
    }

    @Test
    public void testSanitizeImgWithoutRequiredSrc()
        throws Exception
    {
        final String html = "<img srcset='srcSetValue' alt='altValue' custom='customValue' id='idValue'/>";
        final String sanitized = new HtmlRichTextSanitizer().sanitize( html );

        assertEquals( "", sanitized );
    }

    @Test
    public void testTableWithDataCkeAttribute()
        throws Exception
    {
        final String html = "<td data-widget='altValue'></td>";
        final String sanitized = new HtmlRichTextSanitizer().sanitize( html );

        assertEquals( "<table><tbody><tr><td data-widget=\"altValue\"></td></tr></tbody></table>", sanitized );
    }

    @Test
    public void testStyles()
        throws Exception
    {
        final String html = "<div style='background-color:powderblue;invalid-style:value;'></div>";
        final String sanitized = new HtmlRichTextSanitizer().sanitize( html );

        assertEquals( "<div style=\"background-color:powderblue\"></div>", sanitized );
    }

    @Test
    public void testUrlProtocols()
        throws Exception
    {
        String sanitized =
            new HtmlRichTextSanitizer().sanitize( "<p><a href=\"content://197eeb9b-fd85-4799-92c2-ea6d86103c8d\">hrthrt</a></p>" );
        assertEquals( "<p><a href=\"content://197eeb9b-fd85-4799-92c2-ea6d86103c8d\">hrthrt</a></p>", sanitized );

        sanitized = new HtmlRichTextSanitizer().sanitize( "<p><a href=\"media://197eeb9b-fd85-4799-92c2-ea6d86103c8d\">hrthrt</a></p>" );
        assertEquals( "<p><a href=\"media://197eeb9b-fd85-4799-92c2-ea6d86103c8d\">hrthrt</a></p>", sanitized );

        sanitized = new HtmlRichTextSanitizer().sanitize( "<p><a href=\"image://197eeb9b-fd85-4799-92c2-ea6d86103c8d\">hrthrt</a></p>" );
        assertEquals( "<p><a href=\"image://197eeb9b-fd85-4799-92c2-ea6d86103c8d\">hrthrt</a></p>", sanitized );
    }
}
