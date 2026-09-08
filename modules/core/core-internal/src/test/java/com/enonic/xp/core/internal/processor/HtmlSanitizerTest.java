package com.enonic.xp.core.internal.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void testDataAttributesAreCollectedFromAttributeNamesOnly()
    {
        final String html = "<p data-first=\"1\" class=\"data-second\">text</p><p data-second=\"2\">more</p>";
        final String sanitized = InternalHtmlSanitizer.richText().sanitize( html );

        assertEquals( "<p data-first=\"1\" class=\"data-second\">text</p><p data-second=\"2\">more</p>", sanitized );
    }

    @Test
    void testStrictDropsDataAttributes()
    {
        final String sanitized = InternalHtmlSanitizer.strict().sanitize( "<p data-widget=\"1\">text</p>" );

        assertEquals( "<p>text</p>", sanitized );
    }

    @Test
    void testConcurrentSanitizeKeepsDataAttributes()
        throws Exception
    {
        final ExecutorService executor = Executors.newFixedThreadPool( 8 );
        try
        {
            final List<Future<Boolean>> results = new ArrayList<>();
            for ( int i = 0; i < 400; i++ )
            {
                final String attribute = "data-attr" + i;
                results.add( executor.submit( () -> {
                    final String html = "<p " + attribute + "=\"v\">t</p>";
                    return html.equals( InternalHtmlSanitizer.richText().sanitize( html ) );
                } ) );
            }
            for ( final Future<Boolean> result : results )
            {
                assertTrue( result.get( 30, TimeUnit.SECONDS ) );
            }
        }
        finally
        {
            executor.shutdown();
            if ( !executor.awaitTermination( 30, TimeUnit.SECONDS ) )
            {
                executor.shutdownNow();
            }
        }
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
