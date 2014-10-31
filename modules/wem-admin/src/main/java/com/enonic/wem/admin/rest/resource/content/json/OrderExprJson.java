package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.query.expr.OrderExpr;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({                                                                                 //
                  @JsonSubTypes.Type(value = FieldOrderExprJson.class, name = "FieldOrderExpr"),    //
                  @JsonSubTypes.Type(value = DynamicOrderExprJson.class, name = "DynamicOrderExpr") //
              })
public abstract class OrderExprJson
{
    //  private final OrderExpr orderExpr;

    /*
    public OrderExprJson( final OrderExpr orderExpr )
    {
        this.orderExpr = orderExpr;

        if ( orderExpr instanceof FieldOrderExpr )
        {
            new FieldOrderExprJson( (FieldOrderExpr) orderExpr );
        }
        else if ( orderExpr instanceof DynamicOrderExpr )
        {
            new DynamicOrderExprJson( (DynamicOrderExpr) orderExpr );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown order expression-type " + orderExpr.getClass().getName() );
        }
    }

*/
    public abstract OrderExpr getOrderExpr();

}
