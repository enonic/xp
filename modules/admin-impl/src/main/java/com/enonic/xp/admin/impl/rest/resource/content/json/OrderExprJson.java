package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.xp.core.query.expr.OrderExpr;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({                                                                                 //
                  @JsonSubTypes.Type(value = FieldOrderExprJson.class, name = "FieldOrderExpr"),    //
                  @JsonSubTypes.Type(value = DynamicOrderExprJson.class, name = "DynamicOrderExpr") //
              })
public abstract class OrderExprJson
{
    public abstract OrderExpr getOrderExpr();
}
