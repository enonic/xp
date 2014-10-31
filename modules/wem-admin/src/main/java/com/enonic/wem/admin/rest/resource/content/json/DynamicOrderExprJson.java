package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.query.expr.DynamicOrderExpr;
import com.enonic.wem.api.query.expr.FunctionExpr;
import com.enonic.wem.api.query.expr.OrderExpr;

public class DynamicOrderExprJson
    extends OrderExprJson
{
    private final String function;

    private final String direction;

    private final DynamicOrderExpr orderExpr;

    public DynamicOrderExprJson( final DynamicOrderExpr orderExpr )
    {
        // TODO: Fix arguments
        this.function = orderExpr.getFunction().getName();
        this.direction = orderExpr.getDirection().name();
        this.orderExpr = orderExpr;
    }

    @JsonCreator
    public DynamicOrderExprJson( @JsonProperty("function") final String function, //
                                 @JsonProperty("direction") final String direction )
    {
        this.function = function;
        this.direction = direction;

        // TODO: Fix arguments
        this.orderExpr = new DynamicOrderExpr( new FunctionExpr( function, null ), OrderExpr.Direction.valueOf( direction ) );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getFunction()
    {
        return function;
    }


    @Override
    public OrderExpr getOrderExpr()
    {
        return null;
    }
}
