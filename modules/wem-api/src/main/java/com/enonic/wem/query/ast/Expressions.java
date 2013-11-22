package com.enonic.wem.query.ast;

public final class Expressions
{
    public static FieldExpr field( final String name )
    {
        return new FieldExpr( name );
    }
}
