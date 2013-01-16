/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.parser;

public final class FilterExpr
{
    private final String name;

    private final Object[] args;

    public FilterExpr( String name, Object[] args )
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
        StringBuffer str = new StringBuffer();
        str.append( this.name ).append( "(" );

        for ( int i = 0; i < this.args.length; i++ )
        {
            if ( i > 0 )
            {
                str.append( "," );
            }

            str.append( encode( this.args[i] ) );
        }

        str.append( ")" );
        return str.toString();
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
