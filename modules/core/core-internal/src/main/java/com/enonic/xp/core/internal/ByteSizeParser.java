package com.enonic.xp.core.internal;

import java.util.Locale;

public final class ByteSizeParser
{

    private ByteSizeParser()
    {
    }

    private static final long BYTE_FACTOR = 1L;

    private static final long KB_FACTOR = BYTE_FACTOR * 1024L;

    private static final long MB_FACTOR = KB_FACTOR * 1024L;

    private static final long GB_FACTOR = MB_FACTOR * 1024L;

    private static final long TB_FACTOR = GB_FACTOR * 1024L;

    private static final long PB_FACTOR = TB_FACTOR * 1024L;

    public static long parse( final String sValue )
    {
        String postFix = sValue.
            substring( sValue.length() - Math.min( 2, sValue.length() ) ).
            toLowerCase( Locale.ROOT );

        long bytes;

        if ( postFix.endsWith( "k" ) )
        {
            bytes = (long) parseByteValue( sValue, KB_FACTOR, 1 );
        }
        else if ( postFix.endsWith( "kb" ) )
        {
            bytes = (long) ( parseByteValue( sValue, KB_FACTOR, 2 ) );
        }
        else if ( postFix.endsWith( "m" ) )
        {
            bytes = (long) ( parseByteValue( sValue, MB_FACTOR, 1 ) );
        }
        else if ( postFix.endsWith( "mb" ) )
        {
            bytes = (long) ( parseByteValue( sValue, MB_FACTOR, 2 ) );
        }
        else if ( postFix.endsWith( "g" ) )
        {
            bytes = (long) ( parseByteValue( sValue, GB_FACTOR, 1 ) );
        }
        else if ( postFix.endsWith( "gb" ) )
        {
            bytes = (long) ( parseByteValue( sValue, GB_FACTOR, 2 ) );
        }
        else if ( postFix.endsWith( "t" ) )
        {
            bytes = (long) ( parseByteValue( sValue, TB_FACTOR, 1 ) );
        }
        else if ( postFix.endsWith( "tb" ) )
        {
            bytes = (long) ( parseByteValue( sValue, TB_FACTOR, 2 ) );
        }
        else if ( postFix.endsWith( "p" ) )
        {
            bytes = (long) ( parseByteValue( sValue, PB_FACTOR, 1 ) );
        }
        else if ( postFix.endsWith( "pb" ) )
        {
            bytes = (long) ( parseByteValue( sValue, PB_FACTOR, 2 ) );
        }
        else if ( postFix.endsWith( "b" ) )
        {
            bytes = Long.parseLong( sValue.substring( 0, sValue.length() - 1 ) );
        }
        else
        {
            bytes = Long.parseLong( sValue );
        }

        return bytes;
    }

    private static double parseByteValue( final String sValue, final long kbFactor, final int postfixLenght )
    {
        try
        {
            return Double.parseDouble( sValue.substring( 0, sValue.length() - postfixLenght ) ) * kbFactor;
        }
        catch ( NumberFormatException e )
        {
            throw new IllegalArgumentException( "Wrong format of size-value [" + sValue + "]" );
        }
    }
}
