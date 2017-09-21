package com.enonic.xp.extractor.impl;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

class ExtractedTextCleaner
{

    private static final String CONTROL_CHARACTERS_TO_PRESERVE = "\r\n\t";

    private static final String LINE_BREAK_CR_LF = "\\u000D\\u000A";

    private static final String ANY_LINE_BREAK = "[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]";

    private static final String ANY_HORIZONTAL_WHITESPACE = "\\h";

    private final static String LINE_BREAK_AND_WHITESPACE = "(" + LINE_BREAK_CR_LF + "(" + ANY_HORIZONTAL_WHITESPACE + ")*)+";

    private final static String ANY_LINE_BREAK_AND_WHITESPACE = "(" + ANY_LINE_BREAK + "(" + ANY_HORIZONTAL_WHITESPACE + ")*)";

    private final static String LINE_BREAKS = "" + ANY_LINE_BREAK + "+";

    private final static String CONSECUTIVE_HORIZONTAL_WHITESPACE = "(" + ANY_HORIZONTAL_WHITESPACE + ")+";

    private final static String LINE_SEPARATOR = "\n";

    public static String clean( final String value )
    {
        String cleanedText = value;

        if ( Strings.isNullOrEmpty( cleanedText ) )
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
        return CharMatcher.JAVA_ISO_CONTROL.and( allButPreserved ).removeFrom( original );
    }
}
