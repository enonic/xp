package com.enonic.xp.core.impl.content.processor;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

class ExtractedTextCleaner
{

    private static final String CONTROL_CHARACTERS_TO_PRESERVE = "\r\n\t";

    private static final String ANY_LINE_BREAK = "\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]";

    private final static String CONSECUTIVE_LINE_BREAKS = "(" + ANY_LINE_BREAK + ")+";

    private static final String ANY_HORIZONTAL_WHITESPACE = "\\h";

    private final static String CONSECUTIVE_HORIZONTAL_WHITESPACE = "(" + ANY_HORIZONTAL_WHITESPACE + ")+";

    private final static String SYSTEM_LINE_SEPARATOR = System.getProperty( "line.separator" );

    public static String clean( final String value )
    {
        String cleanedText = value;

        if ( Strings.isNullOrEmpty( cleanedText ) )
        {
            return "";
        }

        cleanedText = cleanLineBreaks( cleanedText );
        cleanedText = cleanControlCharacters( cleanedText );

        return cleanedText;
    }

    private static String cleanLineBreaks( final String original )
    {
        return original.replaceAll( CONSECUTIVE_LINE_BREAKS, SYSTEM_LINE_SEPARATOR ).replaceAll( CONSECUTIVE_HORIZONTAL_WHITESPACE, " " );
    }

    private static String cleanControlCharacters( final String original )
    {
        CharMatcher charsToPreserve = CharMatcher.anyOf( CONTROL_CHARACTERS_TO_PRESERVE );
        CharMatcher allButPreserved = charsToPreserve.negate();
        return CharMatcher.JAVA_ISO_CONTROL.and( allButPreserved ).removeFrom( original );
    }
}
