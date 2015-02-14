package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.enonic.xp.core.index.ChildOrder;
import com.enonic.xp.core.query.expr.DynamicOrderExpr;
import com.enonic.xp.core.query.expr.FieldOrderExpr;
import com.enonic.xp.core.query.expr.OrderExpr;

public class ChildOrderJson
{
    private ChildOrder childOrder;

    private List<OrderExprJson> orderExpressions = Lists.newLinkedList();

    public ChildOrderJson( final ChildOrder childOrder )
    {
        for ( final OrderExpr orderExpr : childOrder.getOrderExpressions() )
        {
            if ( orderExpr instanceof FieldOrderExpr )
            {
                orderExpressions.add( new FieldOrderExprJson( (FieldOrderExpr) orderExpr ) );
            }
            else if ( orderExpr instanceof DynamicOrderExpr )
            {
                throw new UnsupportedOperationException( "Not implemented yet" );
                //orderExpressions.add( new DynamicOrderExprJson( (DynamicOrderExpr) orderExpr ) );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported json serializer for type " + orderExpr.getClass().getName() );
            }
        }
    }

    @JsonCreator
    public ChildOrderJson( @JsonProperty("orderExpressions") final List<OrderExprJson> orderExpressions )
    {
        this.orderExpressions = orderExpressions;

        final ChildOrder.Builder builder = ChildOrder.create();

        for ( final OrderExprJson childOrderExpression : orderExpressions )
        {
            builder.add( childOrderExpression.getOrderExpr() );
        }

        this.childOrder = builder.build();
    }

    @JsonIgnore
    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<OrderExprJson> getOrderExpressions()
    {
        return orderExpressions;
    }
}
