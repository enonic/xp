package com.enonic.xp.query.expr;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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
        return this.name + this.arguments.stream().map( Objects::toString ).collect( Collectors.joining( ", ", "(", ")" ) );
    }
}
