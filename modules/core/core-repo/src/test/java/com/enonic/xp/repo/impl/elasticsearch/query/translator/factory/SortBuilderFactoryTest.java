package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.expr.DslOrderExpr;
import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    @Test
    void createFieldSort()
    {
        final Set<OrderExpr> orderExprs = new HashSet<>();
        orderExprs.add( new FieldOrderExpr( FieldExpr.from( "myField" ), OrderExpr.Direction.ASC ) );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( orderExprs );

        assertEquals( 1, sortBuilders.size() );
        assertTrue( sortBuilders.iterator().next() instanceof FieldSortBuilder );
    }

    @Test
    void createFieldWithoutDirectionSort()
    {
        final Set<OrderExpr> orderExprs = new HashSet<>();
        orderExprs.add( new FieldOrderExpr( FieldExpr.from( "myField" ), null ) );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( orderExprs );

        assertEquals( 1, sortBuilders.size() );
        assertTrue( sortBuilders.iterator().next() instanceof FieldSortBuilder );
    }

    @Test
    void createMultipleFieldSort()
    {
        final Set<OrderExpr> orderExprs = new HashSet<>();
        orderExprs.add( new FieldOrderExpr( FieldExpr.from( "myField" ), OrderExpr.Direction.ASC ) );
        orderExprs.add( new FieldOrderExpr( FieldExpr.from( "mySecondField" ), OrderExpr.Direction.DESC ) );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( orderExprs );

        assertEquals( 2, sortBuilders.size() );
        assertTrue( sortBuilders.iterator().next() instanceof FieldSortBuilder );
        assertTrue( sortBuilders.iterator().next() instanceof FieldSortBuilder );
    }

    @Test
    void createFieldSortWithLanguage()
        throws Exception
    {
        final FieldOrderExpr orderExpr =
            FieldOrderExpr.create( IndexPath.from( "myField" ), OrderExpr.Direction.ASC, Locale.forLanguageTag( "no" ) );

        final List<SortBuilder> sortBuilders =
            new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( List.of( orderExpr ) );

        // Collation of the language first, binary order for documents indexed without that language
        assertEquals( 2, sortBuilders.size() );
        assertTrue( toJson( sortBuilders.get( 0 ) )
                        .contains( SearchQueryFieldNameResolver.INSTANCE.resolveOrderByFieldName( IndexPath.from( "myField" ),
                                                                                                Locale.forLanguageTag( "no" ) ) ) );
        assertTrue( toJson( sortBuilders.get( 1 ) )
                        .contains( SearchQueryFieldNameResolver.INSTANCE.resolveOrderByFieldName( IndexPath.from( "myField" ), null ) ) );
    }

    @Test
    void languageFallbackGoesRightAfterItsLanguageSort()
        throws Exception
    {
        final FieldOrderExpr languageOrderExpr =
            FieldOrderExpr.create( IndexPath.from( "myField" ), OrderExpr.Direction.ASC, Locale.forLanguageTag( "no" ) );
        final FieldOrderExpr secondaryOrderExpr = new FieldOrderExpr( FieldExpr.from( "priority" ), OrderExpr.Direction.ASC );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create(
            List.of( languageOrderExpr, secondaryOrderExpr ) );

        assertEquals( 3, sortBuilders.size() );
        assertTrue( toJson( sortBuilders.get( 1 ) ).contains(
            SearchQueryFieldNameResolver.INSTANCE.resolveOrderByFieldName( IndexPath.from( "myField" ), null ) ) );
        assertTrue( toJson( sortBuilders.get( 2 ) ).contains(
            SearchQueryFieldNameResolver.INSTANCE.resolveOrderByFieldName( IndexPath.from( "priority" ), null ) ) );
    }

    @Test
    void createDslFieldSortWithLanguage()
    {
        final PropertyTree expression = new PropertyTree();
        expression.addString( "field", "myField" );
        expression.addString( "direction", "DESC" );
        expression.addString( "language", "no" );

        final List<SortBuilder> sortBuilders =
            new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( List.of( DslOrderExpr.from( expression ) ) );

        assertEquals( 2, sortBuilders.size() );
        assertTrue( sortBuilders.get( 0 ) instanceof FieldSortBuilder );
        assertTrue( sortBuilders.get( 1 ) instanceof FieldSortBuilder );
    }

    @Test
    void createGeoDistance()
    {
        final Set<OrderExpr> orderExprs = new HashSet<>();
        orderExprs.add( new DynamicOrderExpr( new FunctionExpr( "geoDistance",
                                                                List.of( ValueExpr.string( "myField" ), ValueExpr.geoPoint( "-50,40" ),
                                                                         ValueExpr.string( "km" ) ) ), OrderExpr.Direction.ASC ) );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( orderExprs );

        assertEquals( 1, sortBuilders.size() );
        assertTrue( sortBuilders.iterator().next() instanceof GeoDistanceSortBuilder );
    }

    @Test
    void createGeoDistanceWithoutDirection()
    {
        final Set<OrderExpr> orderExprs = new HashSet<>();
        orderExprs.add( new DynamicOrderExpr( new FunctionExpr( "geoDistance",
                                                                List.of( ValueExpr.string( "myField" ), ValueExpr.geoPoint( "-50,40" ),
                                                                         ValueExpr.string( "km" ) ) ), null ) );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( orderExprs );

        assertEquals( 1, sortBuilders.size() );
        assertTrue( sortBuilders.iterator().next() instanceof GeoDistanceSortBuilder );
    }

    @Test
    void createEmpty()
    {
        assertTrue( new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( List.of() ).isEmpty() );
    }

    @Test
    void createDsl()
    {
        final PropertyTree geoExpression = new PropertyTree();
        geoExpression.addString( "type", "geoDistance" );
        geoExpression.addString( "field", "myGeoPoint" );
        geoExpression.addString( "unit", "ft" );
        geoExpression.addString( "direction", "ASC" );
        final PropertySet location1 = geoExpression.addSet( "location" );
        location1.addDouble( "lat", 2.2 );
        location1.addDouble( "lon", 3.3 );

        final PropertyTree geoExpressionWithoutOptional = new PropertyTree();
        geoExpressionWithoutOptional.addString( "type", "geoDistance" );
        geoExpressionWithoutOptional.addString( "field", "myGeoPoint" );
        final PropertySet location2 = geoExpressionWithoutOptional.addSet( "location" );
        location2.addDouble( "lat", 2D );
        location2.addDouble( "lon", 3D );

        final PropertyTree geoExpressionWithoutType = new PropertyTree();
        geoExpressionWithoutType.addString( "field", "myGeoPoint" );
        final PropertySet location3 = geoExpressionWithoutType.addSet( "location" );
        location3.addDouble( "lat", 2D );
        location3.addDouble( "lon", 3D );

        final PropertyTree fieldExpressionWithDirection = new PropertyTree();
        fieldExpressionWithDirection.addString( "field", "myField" );
        fieldExpressionWithDirection.addString( "direction", "DESC" );

        final PropertyTree fieldExpressionWithoutDirection = new PropertyTree();
        fieldExpressionWithoutDirection.addString( "field", "_name" );

        final DslOrderExpr geoOrderExpr = DslOrderExpr.from( geoExpression );
        final DslOrderExpr geoOrderWithoutOptionalExpr = DslOrderExpr.from( geoExpressionWithoutOptional );
        final DslOrderExpr geoOrderWithoutTypeExpr = DslOrderExpr.from( geoExpressionWithoutType );
        final DslOrderExpr fieldWithDirectionOrderExpr = DslOrderExpr.from( fieldExpressionWithDirection );
        final DslOrderExpr fieldWithoutDirectionOrderExpr = DslOrderExpr.from( fieldExpressionWithoutDirection );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create(
            List.of( geoOrderExpr, geoOrderWithoutOptionalExpr, geoOrderWithoutTypeExpr, fieldWithDirectionOrderExpr,
                     fieldWithoutDirectionOrderExpr ) );

        assertEquals( 5, sortBuilders.size() );
        assertTrue( sortBuilders.get( 0 ) instanceof GeoDistanceSortBuilder );
        assertTrue( sortBuilders.get( 1 ) instanceof GeoDistanceSortBuilder );
        assertTrue( sortBuilders.get( 2 ) instanceof GeoDistanceSortBuilder );
        assertTrue( sortBuilders.get( 3 ) instanceof FieldSortBuilder );
        assertTrue( sortBuilders.get( 4 ) instanceof FieldSortBuilder );
    }

    @Test
    void createDslInvalidFunction()
    {
        final PropertyTree geoExpression = new PropertyTree();
        geoExpression.addString( "type", "unknownFunction" );
        geoExpression.addString( "field", "myGeoPoint" );

        final DslOrderExpr unknownOrderExpr = DslOrderExpr.from( geoExpression );

        assertThrows( IllegalArgumentException.class,
                      () -> new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( List.of( unknownOrderExpr ) ) );

    }

    private static String toJson( final SortBuilder sortBuilder )
        throws IOException
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
        sortBuilder.toXContent( builder, ToXContent.EMPTY_PARAMS );
        return builder.endObject().string();
    }
}
