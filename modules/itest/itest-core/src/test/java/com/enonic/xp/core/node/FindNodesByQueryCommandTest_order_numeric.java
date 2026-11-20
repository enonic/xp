package com.enonic.xp.core.node;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.DslOrderExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.util.GeoPoint;

import static org.assertj.core.api.Assertions.assertThat;

class FindNodesByQueryCommandTest_order_numeric
    extends AbstractNodeTest
{
    private Node parent;

    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
        this.parent = createNode( CreateNodeParams.create()
                                      .parent( NodePath.ROOT )
                                      .name( "parent" )
                                      .childOrder( ChildOrder.from( "price ASC" ) )
                                      .build() );
    }

    @Test
    void mixed_long_and_double_sort_by_value()
    {
        createMixedPrices();

        assertThat( orderBy( "price ASC" ) ).containsExactly( "l-5", "d-1.5", "d2.5", "l3", "l100", "d100.5" );
        assertThat( orderBy( "price DESC" ) ).containsExactly( "d100.5", "l100", "l3", "d2.5", "d-1.5", "l-5" );
    }

    @Test
    void equal_values_are_ordered_by_type_in_the_sort_direction()
    {
        child( "l3", d -> d.setLong( "price", 3L ) );
        child( "d3", d -> d.setDouble( "price", 3.0 ) );
        nodeService.refresh( RefreshMode.ALL );

        assertThat( orderBy( "price ASC" ) ).containsExactly( "d3", "l3" );
        assertThat( orderBy( "price DESC" ) ).containsExactly( "l3", "d3" );
    }

    @Test
    void longs_above_double_precision_keep_their_order()
    {
        final long twoPow53 = 1L << 53;
        child( "plus-2", d -> d.setLong( "price", twoPow53 + 2 ) );
        child( "plus-0", d -> d.setLong( "price", twoPow53 ) );
        child( "plus-1", d -> d.setLong( "price", twoPow53 + 1 ) );
        nodeService.refresh( RefreshMode.ALL );

        assertThat( orderBy( "price ASC" ) ).containsExactly( "plus-0", "plus-1", "plus-2" );
        assertThat( orderBy( "price DESC" ) ).containsExactly( "plus-2", "plus-1", "plus-0" );
    }

    @Test
    void strings_and_missing_values_after_numbers()
    {
        child( "l3", d -> d.setLong( "price", 3L ) );
        child( "d2.5", d -> d.setDouble( "price", 2.5 ) );
        child( "s-abc", d -> d.setString( "price", "abc" ) );
        child( "s-7", d -> d.setString( "price", "7" ) );
        child( "none", d -> {
        } );
        nodeService.refresh( RefreshMode.ALL );

        assertThat( orderBy( "price ASC" ) ).containsExactly( "d2.5", "l3", "s-7", "s-abc", "none" );
        assertThat( orderBy( "price DESC" ) ).containsExactly( "l3", "d2.5", "s-abc", "s-7", "none" );
    }

    @Test
    void arrays_sort_by_lowest_value_ascending_and_highest_descending()
    {
        child( "l3", d -> d.setLong( "price", 3L ) );
        child( "long-50-1", d -> {
            d.addLong( "price", 50L );
            d.addLong( "price", 1L );
        } );
        child( "double-0.25-90.5", d -> {
            d.addDouble( "price", 0.25 );
            d.addDouble( "price", 90.5 );
        } );
        nodeService.refresh( RefreshMode.ALL );

        assertThat( orderBy( "price ASC" ) ).containsExactly( "double-0.25-90.5", "long-50-1", "l3" );
        assertThat( orderBy( "price DESC" ) ).containsExactly( "double-0.25-90.5", "long-50-1", "l3" );
    }

    @Test
    void non_numeric_fields_sort_by_order_by_value()
    {
        child( "a", d -> {
            d.setString( "title", "cherry" );
            d.setInstant( "published", Instant.parse( "2020-01-02T00:00:00Z" ) );
        } );
        child( "b", d -> {
            d.setString( "title", "Banana" );
            d.setInstant( "published", Instant.parse( "2020-01-03T00:00:00Z" ) );
        } );
        child( "c", d -> {
            d.setString( "title", "apple" );
            d.setInstant( "published", Instant.parse( "2020-01-01T00:00:00Z" ) );
        } );
        nodeService.refresh( RefreshMode.ALL );

        assertThat( orderBy( "title ASC" ) ).containsExactly( "c", "b", "a" );
        assertThat( orderBy( "published DESC" ) ).containsExactly( "b", "a", "c" );
    }

    @Test
    void child_order_sorts_by_value()
    {
        createMixedPrices();

        final NodeIds children = findByParent( parent.path() ).getNodeIds();

        assertThat( names( children ) ).containsExactly( "l-5", "d-1.5", "d2.5", "l3", "l100", "d100.5" );
    }

    @Test
    void dsl_field_sort_sorts_by_value()
    {
        createMixedPrices();

        final PropertyTree expression = new PropertyTree();
        expression.addString( "field", "price" );
        expression.addString( "direction", "DESC" );

        assertThat( query( DslOrderExpr.from( expression ) ) ).containsExactly( "d100.5", "l100", "l3", "d2.5", "d-1.5", "l-5" );
    }

    @Test
    void language_sort_sorts_numbers_by_value()
    {
        createMixedPrices();

        final FieldOrderExpr orderExpr =
            FieldOrderExpr.create( IndexPath.from( "price" ), OrderExpr.Direction.ASC, Locale.forLanguageTag( "no" ) );

        assertThat( query( orderExpr ) ).containsExactly( "l-5", "d-1.5", "d2.5", "l3", "l100", "d100.5" );
    }

    @Test
    void sort_values_hold_one_value_per_order_expression()
    {
        child( "l3", d -> {
            d.setString( "title", "apple" );
            d.setLong( "price", 3L );
            d.setGeoPoint( "location", GeoPoint.from( "1,1" ) );
        } );
        nodeService.refresh( RefreshMode.ALL );

        final List<Object> values =
            sortValues( doQuery( "_parentPath = '/parent' ORDER BY title ASC, price DESC, geoDistance('location', '0,0')" ) );

        assertThat( values ).hasSize( 3 );
        assertThat( values.get( 0 ) ).hasToString( "apple" );
        assertThat( values.get( 2 ) ).isInstanceOf( Double.class );
        assertThat( values ).noneMatch( value -> value instanceof Double number && number.isInfinite() );
    }

    @Test
    void language_sort_values_hold_one_value()
    {
        child( "a", d -> d.setString( "title", "apple" ) );
        nodeService.refresh( RefreshMode.ALL );

        final FieldOrderExpr orderExpr =
            FieldOrderExpr.create( IndexPath.from( "title" ), OrderExpr.Direction.ASC, Locale.forLanguageTag( "no" ) );

        assertThat( sortValues( doFindByQuery( NodeQuery.create().parent( parent.path() ).addOrderBy( orderExpr ).build() ) ) ).hasSize(
            1 );
    }

    private List<Object> sortValues( final FindNodesByQueryResult result )
    {
        return result.getNodeHits().iterator().next().getSort().getValues();
    }

    private void createMixedPrices()
    {
        child( "l-5", d -> d.setLong( "price", -5L ) );
        child( "l3", d -> d.setLong( "price", 3L ) );
        child( "l100", d -> d.setLong( "price", 100L ) );
        child( "d-1.5", d -> d.setDouble( "price", -1.5 ) );
        child( "d2.5", d -> d.setDouble( "price", 2.5 ) );
        child( "d100.5", d -> d.setDouble( "price", 100.5 ) );
        nodeService.refresh( RefreshMode.ALL );
    }

    private void child( final String name, final Consumer<PropertyTree> data )
    {
        final PropertyTree tree = new PropertyTree();
        data.accept( tree );
        createNode( CreateNodeParams.create().parent( parent.path() ).name( name ).data( tree ).build() );
    }

    private List<String> orderBy( final String orderBy )
    {
        return names( doQuery( "_parentPath = '/parent' ORDER BY " + orderBy ).getNodeIds() );
    }

    private List<String> query( final OrderExpr orderExpr )
    {
        final NodeQuery query = NodeQuery.create().parent( parent.path() ).addOrderBy( orderExpr ).size( -1 ).build();
        return names( doFindByQuery( query ).getNodeIds() );
    }

    private List<String> names( final NodeIds nodeIds )
    {
        return getNodes( nodeIds ).stream().map( node -> node.name().toString() ).toList();
    }
}
