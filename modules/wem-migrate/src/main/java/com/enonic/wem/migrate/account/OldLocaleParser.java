package com.enonic.wem.migrate.account;

import java.util.Locale;

abstract class OldLocaleParser
{
    public static Locale parseLocale( final String value )
    {
        String normalized = supportBothUnderscoreAndDash( value );

        final String[] parts = normalized.split( "_" );

        if ( parts.length == 1 )
        {
            return new Locale( parts[0] );
        }
        else if ( parts.length == 2 )
        {
            return new Locale( parts[0], parts[1] );
        }
        else if ( parts.length == 3 )
        {
            return new Locale( parts[0], parts[1], parts[2] );
        }

        throw new IllegalArgumentException( "Could not parse string: '" + value + "' to a valid locale" );
    }

    private static String supportBothUnderscoreAndDash( String value )
    {
        return value.replace( "-", "_" );
    }
}
