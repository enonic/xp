package com.enonic.wem.query;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public final class OrderBy
    implements Expression
{
    private final OrderSpec[] orderList;

    public OrderBy( final OrderSpec... orderList )
    {
        Preconditions.checkNotNull( orderList, "OrderList cannot be null" );
        Preconditions.checkArgument( orderList.length == 0, "OrderList cannot be empty" );
        this.orderList = orderList;
    }

    public OrderSpec[] getOrderList()
    {
        return this.orderList;
    }

    @Override
    public String toString()
    {
        return "ORDER BY " + Joiner.on( " " ).join( this.orderList );
    }
}
