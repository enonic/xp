package com.enonic.wem.api.query.expr;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public final class FunctionExpr
    implements Expression
{
    private final String name;

    private final ImmutableList<ValueExpr> arguments;

    public FunctionExpr( final String name, final Iterable<ValueExpr> arguments )
    {
        this.name = name;
        this.arguments = ImmutableList.copyOf( arguments );
    }

    public static FunctionExpr from( final String functionName, final Iterable<ValueExpr> arguments )
    {
        return new FunctionExpr( functionName, arguments );
    }

    public static FunctionExpr from( final String functionName, ValueExpr... arguments )
    {
        return new FunctionExpr( functionName, Arrays.asList( arguments ) );
    }

    public String getName()
    {
        return this.name;
    }

    public List<ValueExpr> getArguments()
    {
        return this.arguments;
    }

    @Override
    public String toString()
    {
        final StringBuilder str = new StringBuilder( this.name );
        str.append( "(" );
        str.append( Joiner.on( ", " ).join( this.arguments ) );
        str.append( ")" );
        return str.toString();
    }
}
