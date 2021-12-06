package com.enonic.xp.query.expr;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;

@PublicApi
public final class DslExpr
    implements ConstraintExpr
{
    private final PropertyTree expression;

    private DslExpr( final PropertyTree expression )
    {
        this.expression = expression;
    }

    public static DslExpr from( final PropertyTree expression )
    {
        return new DslExpr( expression );
    }

    public PropertyTree getExpression()
    {
        return expression;
    }
}
