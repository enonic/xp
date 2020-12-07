package com.enonic.xp.core.internal;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

/**
 * Internal utility methods to work with HTML
 */
public final class HtmlHelper
{
    private HtmlHelper()
    {
    }

    /**
     * Unescapes HTML entities from a string
     *
     * @param html HTML escaped string
     * @return an unescaped string
     */
    public static String unescape( String html )
    {
        if ( html.isBlank() )
        {
            return html;
        }
        return Parser.unescapeEntities( html, false );
    }

    /**
     * Extracts text from HTML. Whitespace is normalized and trimmed.
     *
     * @param html string with HTML, can't be null
     * @return extracted text
     */
    public static String htmlToText( String html )
    {
        if ( html.isBlank() )
        {
            return "";
        }
        return Jsoup.parse( html ).text();
    }
}
