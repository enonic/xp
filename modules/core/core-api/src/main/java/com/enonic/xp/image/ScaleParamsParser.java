package com.enonic.xp.image;

import java.util.Arrays;

import static com.google.common.base.Strings.nullToEmpty;

public final class ScaleParamsParser
{
    public ScaleParams parse( final String value )
    {
        String name = parseName( value );

        if ( name != null )
        {
            Object[] args = parseArguments( value );
            return new ScaleParams( name, args );
        }
        else
        {
            return null;
        }
    }

    private String parseName( String str )
    {
        if ( str == null )
        {
            return null;
        }

        int pos = str.indexOf( '-' );
        if ( pos >= 0 )
        {
            str = str.substring( 0, pos );
        }

        str = str.trim();
        if ( !str.isEmpty() )
        {
            return str;
        }
        else
        {
            return null;
        }
    }

    private static Object[] parseArguments( String str )
    {
        String[] args = str.split( "-" );
        if ( args.length == 1 )
        {
            return new Object[0];
        }

        return Arrays.stream( args ).skip( 1 ).map( ScaleParamsParser::parseIntegerValue ).toArray( Object[]::new );
    }

    private static Integer parseIntegerValue( final String str )
    {
        if ( nullToEmpty( str ).isBlank() )
        {
            return null;
        }
        try
        {
            return Integer.valueOf( str.trim() );
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}
