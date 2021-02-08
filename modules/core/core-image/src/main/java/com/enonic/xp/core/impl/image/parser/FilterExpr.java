/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.parser;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterExpr
{
    private final String name;

    private final Object[] args;

    FilterExpr( String name, Object[] args )
    {
        this.name = name;
        this.args = args;
    }

    public String getName()
    {
        return this.name;
    }

    public Object[] getArguments()
    {
        return this.args;
    }

    @Override
    public String toString()
    {
        return this.name + Stream.of( this.args ).
            map( this::encode ).
            collect( Collectors.joining( ",", "(", ")" ) );
    }

    private String encode( Object arg )
    {
        if ( arg == null )
        {
            return "";
        }

        if ( arg instanceof String )
        {
            return quote( (String) arg );
        }
        else
        {
            return arg.toString();
        }
    }

    private String quote( String arg )
    {
        if ( arg.contains( "'" ) )
        {
            return "\"" + arg + "\"";
        }
        else
        {
            return "'" + arg + "'";
        }
    }
}
