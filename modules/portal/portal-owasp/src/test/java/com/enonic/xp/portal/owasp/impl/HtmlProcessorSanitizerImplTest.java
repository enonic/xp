package com.enonic.xp.portal.owasp.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlProcessorSanitizerImplTest
{

    @Test
    public void testSanitizeImg()
        throws Exception
    {
        final String html = "<img srcset='srcSetValue' alt='altValue' custom='customValue' src='sourceValue' id='idValue'/>";
        final String sanitized = new HtmlProcessorSanitizerImpl().sanitizeHtml( html );

        assertEquals( "<img alt=\"altValue\" src=\"sourceValue\" />", sanitized );
    }

    @Test
    public void testSanitizeImgWithoutRequiredSrc()
        throws Exception
    {
        final String html = "<img srcset='srcSetValue' alt='altValue' custom='customValue' id='idValue'/>";
        final String sanitized = new HtmlProcessorSanitizerImpl().sanitizeHtml( html );

        assertEquals( "", sanitized );
    }

    @Test
    public void testTableWithDataCkeAttribute()
        throws Exception
    {
        final String html = "<td data-widget='altValue'></td>";
        final String sanitized = new HtmlProcessorSanitizerImpl().sanitizeHtml( html );

        assertEquals( "<table><tbody><tr><td data-widget=\"altValue\"></td></tr></tbody></table>", sanitized );
    }

    @Test
    public void testStyles()
        throws Exception
    {
        final String html = "<div style='background-color:powderblue;invalid-style:value;'></div>";
        final String sanitized = new HtmlProcessorSanitizerImpl().sanitizeHtml( html );

        assertEquals( "<div style=\"background-color:powderblue\"></div>", sanitized );
    }
}
