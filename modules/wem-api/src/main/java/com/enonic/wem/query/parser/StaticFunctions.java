package com.enonic.wem.query.parser;

import java.util.List;

import com.enonic.wem.query.expr.FunctionExpr;
import com.enonic.wem.query.expr.ValueExpr;

final class StaticFunctions
{
    private static ValueExpr geoPoint( final ValueExpr arg )
    {
        return ValueExpr.geoPoint( arg.getValue().asString() );
    }

    private static ValueExpr dateTime( final ValueExpr arg )
    {
        return ValueExpr.dateTime( arg.getValue().asString() );
    }

    public static ValueExpr execute( final FunctionExpr function )
    {
        return execute( function.getName(), function.getArguments() );
    }

    private static ValueExpr execute( final String name, final List<ValueExpr> args )
    {
        if ( name.equals( "geoPoint" ) && ( args.size() == 1 ) )
        {
            return geoPoint( args.get( 0 ) );
        }

        if ( name.equals( "dateTime" ) && ( args.size() == 1 ) )
        {
            return dateTime( args.get( 0 ) );
        }

        throw new IllegalArgumentException( "Value function [" + name + "] with [" + args.size() + "] arguments does not exist." );
    }
}
