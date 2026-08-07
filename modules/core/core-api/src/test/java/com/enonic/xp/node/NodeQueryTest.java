package com.enonic.xp.node;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeQueryTest
{
    @Test
    void return_fields_empty_by_default()
    {
        assertTrue( NodeQuery.create().build().getReturnFields().isEmpty() );
    }

    @Test
    void return_fields_accumulate_and_deduplicate()
    {
        final NodeQuery query = NodeQuery.create()
            .returnFields( NodeIndexPath.PATH, NodeIndexPath.NAME )
            .returnFields( IndexPath.from( "_PATH" ) )
            .build();

        assertEquals( Set.of( NodeIndexPath.PATH, NodeIndexPath.NAME ), query.getReturnFields() );
    }

    @Test
    void return_fields_are_limited_to_the_supported_set()
    {
        final NodeQuery.Builder builder = NodeQuery.create();

        assertEquals( "unsupported return field: data.myfield",
                      assertThrows( IllegalArgumentException.class,
                                    () -> builder.returnFields( IndexPath.from( "data.myField" ) ) ).getMessage() );
        assertThrows( IllegalArgumentException.class, () -> builder.returnFields( IndexPath.from( "_manualordervalue" ) ) );
    }

    @Test
    void copy_keeps_every_setting()
    {
        final NodeQuery source = NodeQuery.create()
            .query( QueryParser.parse( "_name = 'a' order by _name asc" ) )
            .addOrderBy( FieldOrderExpr.create( NodeIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) )
            .addQueryFilter( ExistsFilter.create().fieldName( "myfield" ).build() )
            .parent( new NodePath( "/my-parent" ) )
            .returnFields( NodeIndexPath.NAME )
            .from( 5 )
            .size( 42 )
            .batchSize( 100 )
            .explain( true )
            .build();

        final NodeQuery copy = NodeQuery.create( source ).build();

        assertEquals( source.getQuery().toString(), copy.getQuery().toString() );
        // the copy must not duplicate the order carried by the query expression itself
        assertEquals( source.getOrderBys(), copy.getOrderBys() );
        assertEquals( source.getQueryFilters().getSize(), copy.getQueryFilters().getSize() );
        assertEquals( source.getParent(), copy.getParent() );
        assertEquals( source.getReturnFields(), copy.getReturnFields() );
        assertEquals( source.getFrom(), copy.getFrom() );
        assertEquals( source.getSize(), copy.getSize() );
        assertEquals( source.getBatchSize(), copy.getBatchSize() );
        assertEquals( source.isExplain(), copy.isExplain() );
    }

    @Test
    void copy_allows_overriding_order()
    {
        final NodeQuery source = NodeQuery.create().parent( new NodePath( "/my-parent" ) ).build();

        final NodeQuery reordered = NodeQuery.create( source )
            .setOrderExpressions(
                OrderExpressions.from( FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.DESC ) ) )
            .build();

        assertTrue( source.getOrderBys().isEmpty() );
        assertEquals( 1, reordered.getOrderBys().size() );
        assertEquals( source.getParent(), reordered.getParent() );
    }
}
