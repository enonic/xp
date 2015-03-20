package com.enonic.xp.query.parser;

import java.util.List;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.ValueExpr;

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

    private static ValueExpr instant( final ValueExpr arg )
    {
        return ValueExpr.instant( arg.getValue().asString() );
    }

    private static ValueExpr localDateTime( final ValueExpr arg )
    {
        return ValueExpr.localDateTime( arg.getValue().asString() );
    }

    private static ValueExpr time( final ValueExpr arg )
    {
        return ValueExpr.time( arg.getValue().asString() );
    }

    private static ValueExpr date( final ValueExpr arg )
    {
        return ValueExpr.date( arg.getValue().asString() );
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

        if ( name.equals( "instant" ) && ( args.size() == 1 ) )
        {
            return instant( args.get( 0 ) );
        }

        if ( name.equals( "time" ) && ( args.size() == 1 ) )
        {
            return time( args.get( 0 ) );
        }

        if ( name.equals( "date" ) && ( args.size() == 1 ) )
        {
            return date( args.get( 0 ) );
        }

        if ( name.equals( "dateTime" ) && ( args.size() == 1 ) )
        {
            return dateTime( args.get( 0 ) );
        }

        if ( name.equals( "localDateTime" ) && ( args.size() == 1 ) )
        {
            return localDateTime( args.get( 0 ) );
        }

        throw new IllegalArgumentException( "Value function [" + name + "] with [" + args.size() + "] arguments does not exist." );
    }
}
