package com.enonic.wem.query.expr;

import com.enonic.wem.query.OrderSpec;
import com.enonic.wem.query.order.ScoreOrder;

public class ScoreFieldExpr
    extends FieldExpr
{
    public ScoreFieldExpr()
    {
        super("SCORE()");
    }

    public OrderSpec toOrder( final OrderSpec.Direction direction )
    {
        return new ScoreOrder( direction );
    }


}
