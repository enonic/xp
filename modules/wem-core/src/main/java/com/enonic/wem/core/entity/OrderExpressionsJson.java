package com.enonic.wem.core.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public class OrderExpressionsJson
{
    private String orderExpressions;

    @JsonCreator
    public OrderExpressionsJson( final String orderExpressions )
    {
        this.orderExpressions = orderExpressions;
    }


    @SuppressWarnings("UnusedDeclaration")
    public String getOrderExpressions()
    {
        return orderExpressions;
    }
}
