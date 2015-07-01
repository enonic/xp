package com.enonic.xp.image.scale;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public final class ScaleParams
{
    private final String name;

    private final Object[] args;

    public ScaleParams( String name, Object[] args )
    {
        this.name = name;
        this.args = args != null ? args : new Object[0];
    }

    public String getName()
    {
        return this.name;
    }

    public Object[] getArguments()
    {
        return this.args;
    }

    public String toString()
    {
        return this.name + "(" + Stream.of( this.args ).map( this::encode ).collect( joining( "," ) ) + ")";
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
