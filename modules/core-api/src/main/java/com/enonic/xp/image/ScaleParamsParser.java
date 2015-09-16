package com.enonic.xp.image;

import java.util.ArrayList;

public final class ScaleParamsParser
{
    public ScaleParams parse(String value) {

        String name = parseName( value );
        Object[] args = parseArguments( value );

        if ( name != null )
        {
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
        if ( str.length() > 0 )
        {
            return str;
        }
        else
        {
            return null;
        }
    }

    private Object[] parseArguments( String str )
    {
        if ( str == null )
        {
            return null;
        }

        String[] args = str.split( "-" );
        if(args.length == 1)
        {
            return null;
        }

        ArrayList<Object> list = new ArrayList<Object>();
        for(int i = 1 ; i < args.length; i++) {
            list.add( parseIntegerValue( args[i] ) );
        }

        return list.toArray( new Object[list.size()] );
    }

    private Integer parseIntegerValue( String str )
    {
        if ( str == null )
        {
            return null;
        }

        str = str.trim();
        if ( str.length() == 0 )
        {
            return null;
        }

        try
        {
            return new Integer( str );
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}
