package com.enonic.xp.core.internal.processor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlSanitizerTest
{

    @Test
    void testSanitizeImg()
    {
        final String html = "<img srcset='srcSetValue' alt='altValue' custom='customValue' src='sourceValue' id='idValue'/>";
        final String sanitized = InternalHtmlSanitizer.richText().sanitize( html );

        assertEquals( "<img alt=\"altValue\" src=\"sourceValue\" />", sanitized );
    }

    @Test
    void testSanitizeImgWithoutRequiredSrc()
    {
        final String html = "<img srcset='srcSetValue' alt='altValue' custom='customValue' id='idValue'/>";
        final String sanitized = InternalHtmlSanitizer.richText().sanitize( html );

        assertEquals( "", sanitized );
    }

    @Test
    void testTableWithDataCkeAttribute()
    {
        final String html = "<td data-widget='altValue'></td>";
        final String sanitized = InternalHtmlSanitizer.richText().sanitize( html );

        assertEquals( "<table><tbody><tr><td data-widget=\"altValue\"></td></tr></tbody></table>", sanitized );
    }

    @Test
    void testStyles()
    {
        final String html = "<div style='background-color:powderblue;invalid-style:value;'></div>";
        final String sanitized = InternalHtmlSanitizer.richText().sanitize( html );

        assertEquals( "<div style=\"background-color:powderblue\"></div>", sanitized );
    }

    @Test
    void testUrlProtocols()
    {
        String sanitized =
            InternalHtmlSanitizer.richText().sanitize( "<p><a href=\"content://197eeb9b-fd85-4799-92c2-ea6d86103c8d\">hrthrt</a></p>" );
        assertEquals( "<p><a href=\"content://197eeb9b-fd85-4799-92c2-ea6d86103c8d\">hrthrt</a></p>", sanitized );

        sanitized =
            InternalHtmlSanitizer.richText().sanitize( "<p><a href=\"media://197eeb9b-fd85-4799-92c2-ea6d86103c8d\">hrthrt</a></p>" );
        assertEquals( "<p><a href=\"media://197eeb9b-fd85-4799-92c2-ea6d86103c8d\">hrthrt</a></p>", sanitized );

        sanitized =
            InternalHtmlSanitizer.richText().sanitize( "<p><a href=\"image://197eeb9b-fd85-4799-92c2-ea6d86103c8d\">hrthrt</a></p>" );
        assertEquals( "<p><a href=\"image://197eeb9b-fd85-4799-92c2-ea6d86103c8d\">hrthrt</a></p>", sanitized );
    }
}
