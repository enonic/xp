package com.enonic.xp.extractor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;

import static com.google.common.base.Strings.isNullOrEmpty;

class ExtractedTextCleaner
{
    private static final Logger LOG = LoggerFactory.getLogger( ExtractedTextCleaner.class );

    private static final String CONTROL_CHARACTERS_TO_PRESERVE = "\r\n\t";

    private static final String LINE_BREAK_CR_LF = "\\u000D\\u000A";

    private static final String ANY_LINE_BREAK = "[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]";

    private static final String ANY_HORIZONTAL_WHITESPACE = "\\h";

    private static final String LINE_BREAK_AND_WHITESPACE = "(" + LINE_BREAK_CR_LF + "(" + ANY_HORIZONTAL_WHITESPACE + ")*)+";

    private static final String ANY_LINE_BREAK_AND_WHITESPACE = "(" + ANY_LINE_BREAK + "(" + ANY_HORIZONTAL_WHITESPACE + ")*)";

    private static final String LINE_BREAKS = "" + ANY_LINE_BREAK + "+";

    private static final String CONSECUTIVE_HORIZONTAL_WHITESPACE = "(" + ANY_HORIZONTAL_WHITESPACE + ")+";

    private static final String LINE_SEPARATOR = "\n";

    static String clean( final String value )
    {
        try
        {
            return cleanAll( value );
        }
        catch ( Throwable t )
        {
            LOG.warn( "Error cleaning up extracted text", t );
        }
        return value;
    }

    private static String cleanAll( final String value )
    {
        String cleanedText = value;

        if ( isNullOrEmpty( cleanedText ) )
        {
            return "";
        }

        cleanedText = cleanLineBreaks( cleanedText );
        cleanedText = cleanControlCharacters( cleanedText );
        cleanedText = replaceNewLineWithSpace( cleanedText );

        return cleanedText;
    }

    private static String cleanLineBreaks( final String original )
    {
        String result = original.replaceAll( LINE_BREAK_AND_WHITESPACE, LINE_SEPARATOR );

        String previous = null;
        while ( !result.equals( previous ) )
        {
            previous = result;
            result = result.replaceAll( ANY_LINE_BREAK_AND_WHITESPACE, LINE_SEPARATOR );
        }

        result = result.replaceAll( LINE_BREAKS, LINE_SEPARATOR );
        return result.replaceAll( CONSECUTIVE_HORIZONTAL_WHITESPACE, " " );
    }

    private static String replaceNewLineWithSpace( final String original )
    {
        return original.replaceAll( ANY_LINE_BREAK, " " );
    }

    private static String cleanControlCharacters( final String original )
    {
        CharMatcher charsToPreserve = CharMatcher.anyOf( CONTROL_CHARACTERS_TO_PRESERVE );
        CharMatcher allButPreserved = charsToPreserve.negate();
        return CharMatcher.javaIsoControl().and( allButPreserved ).removeFrom( original );
    }
}
