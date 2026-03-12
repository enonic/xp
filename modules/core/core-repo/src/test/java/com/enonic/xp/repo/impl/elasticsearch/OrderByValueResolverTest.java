package com.enonic.xp.repo.impl.elasticsearch;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.repo.impl.node.NodeManualOrderValueResolver;

import static org.assertj.core.api.Assertions.assertThat;

class OrderByValueResolverTest
{
    @Test
    void testName()
    {
        List<String> orderStrings = new ArrayList<>();
        final String third = OrderByValueResolver.getOrderByValue(
            ValueFactory.newDouble( (double) NodeManualOrderValueResolver.before( NodeManualOrderValueResolver.first() ) ) );
        orderStrings.add( third );
        final String second =
            OrderByValueResolver.getOrderByValue( ValueFactory.newDouble( (double) NodeManualOrderValueResolver.first() ) );
        orderStrings.add( second );
        final String first = OrderByValueResolver.getOrderByValue(
            ValueFactory.newDouble( (double) NodeManualOrderValueResolver.after( NodeManualOrderValueResolver.first() ) ) );
        orderStrings.add( first );
        Collections.sort( orderStrings );

        assertThat( orderStrings ).containsExactly( first, second, third );
    }

    @Test
    void testInstant()
    {
        final Instant early = Instant.parse( "2020-01-01T00:00:00.000001Z" );
        final Instant earlyMillis = Instant.parse( "2020-01-01T00:00:00.001Z" );
        final Instant middle = Instant.parse( "2023-06-15T12:30:00Z" );
        final Instant late = Instant.parse( "2026-12-31T23:59:59.999999Z" );

        final String earlyStr = OrderByValueResolver.getOrderByValue( ValueFactory.newDateTime( early ) );
        final String earlyMillisStr = OrderByValueResolver.getOrderByValue( ValueFactory.newDateTime( earlyMillis ) );
        final String middleStr = OrderByValueResolver.getOrderByValue( ValueFactory.newDateTime( middle ) );
        final String lateStr = OrderByValueResolver.getOrderByValue( ValueFactory.newDateTime( late ) );

        List<String> orderStrings = new ArrayList<>( List.of( lateStr, earlyStr, earlyMillisStr, middleStr ) );
        Collections.sort( orderStrings );

        assertThat( orderStrings ).containsExactly( earlyStr, earlyMillisStr, middleStr, lateStr );
    }
}
