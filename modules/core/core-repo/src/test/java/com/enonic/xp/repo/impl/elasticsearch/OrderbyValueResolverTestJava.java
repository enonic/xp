package com.enonic.xp.repo.impl.elasticsearch;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.repo.impl.entity.NodeManualOrderValueResolver;

import static org.junit.Assert.*;

public class OrderbyValueResolverTestJava
{

    @Test
    public void testName()
        throws Exception
    {

        List<String> orderStrings = Lists.newArrayList();
        final String third = OrderbyValueResolver.getOrderbyValue(
            ValueFactory.newDouble( (double) NodeManualOrderValueResolver.START_ORDER_VALUE + NodeManualOrderValueResolver.ORDER_SPACE ) );
        orderStrings.add( third );
        final String second =
            OrderbyValueResolver.getOrderbyValue( ValueFactory.newDouble( (double) NodeManualOrderValueResolver.START_ORDER_VALUE ) );
        orderStrings.add( second );
        final String first = OrderbyValueResolver.getOrderbyValue(
            ValueFactory.newDouble( (double) NodeManualOrderValueResolver.START_ORDER_VALUE - NodeManualOrderValueResolver.ORDER_SPACE ) );
        orderStrings.add( first );
        Collections.sort( orderStrings );

        final Iterator<String> iterator = orderStrings.iterator();
        assertEquals( first, iterator.next() );
        assertEquals( second, iterator.next() );
        assertEquals( third, iterator.next() );

    }

}