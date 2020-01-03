package com.enonic.xp.util;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class CamelCaseConverter
{
    private static final String[] DEFAULT_ILLEGAL_CHARS = new String[]{".", ":", "/", "-", "&", "="};

    private final String[] illegalChars;

    public CamelCaseConverter( final String[] illegalChars )
    {
        this.illegalChars = illegalChars;
    }

    public String toCamelCase( final String s )
    {
        String result = s;
        for ( final String illegalChar : illegalChars )
        {
            if ( result.contains( illegalChar ) )
            {
                result = result.replaceAll( illegalChar, " " );
            }
        }

        result = camelCaseOnChar( result, " " );
        return result;
    }

    private String camelCaseOnChar( final String s, final String c )
    {
        final String[] parts = s.split( c );
        StringBuilder result = new StringBuilder();
        for ( int i = 0; i < parts.length; i++ )
        {
            final String part = parts[i];
            if ( part.length() == 0 )
            {
                continue;
            }
            else if ( startsWithUpperCase( part ) )
            {
                result.append( part );

            }
            else if ( i == 0 )
            {
                result.append( part.toLowerCase() );
            }
            else
            {
                result.append( capitalizeFirst( part ) );
            }
        }
        return result.toString();
    }

    private boolean startsWithUpperCase( final String s )
    {
        return s.substring( 0, 1 ).equalsIgnoreCase( s );
    }

    private String capitalizeFirst( final String s )
    {
        return s.substring( 0, 1 ).toUpperCase() + s.substring( 1 ).toLowerCase();
    }

    public static String defaultConvert( final String s )
    {
        return new CamelCaseConverter( DEFAULT_ILLEGAL_CHARS ).toCamelCase( s );
    }
}
