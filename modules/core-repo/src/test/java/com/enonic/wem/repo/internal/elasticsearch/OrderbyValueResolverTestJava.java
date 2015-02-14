package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.core.data.Value;
import com.enonic.wem.repo.internal.entity.NodeManualOrderValueResolver;

import static org.junit.Assert.*;

public class OrderbyValueResolverTestJava
{

    @Test
    public void testName()
        throws Exception
    {

        List<String> orderStrings = Lists.newArrayList();
        final String third = OrderbyValueResolver.getOrderbyValue(
            Value.newDouble( (double) NodeManualOrderValueResolver.START_ORDER_VALUE + NodeManualOrderValueResolver.ORDER_SPACE ) );
        orderStrings.add( third );
        final String second =
            OrderbyValueResolver.getOrderbyValue( Value.newDouble( (double) NodeManualOrderValueResolver.START_ORDER_VALUE ) );
        orderStrings.add( second );
        final String first = OrderbyValueResolver.getOrderbyValue(
            Value.newDouble( (double) NodeManualOrderValueResolver.START_ORDER_VALUE - NodeManualOrderValueResolver.ORDER_SPACE ) );
        orderStrings.add( first );
        Collections.sort( orderStrings );

        final Iterator<String> iterator = orderStrings.iterator();
        assertEquals( first, iterator.next() );
        assertEquals( second, iterator.next() );
        assertEquals( third, iterator.next() );

    }

}