package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.StoreQueryFieldNameResolver;

import static org.assertj.core.api.Assertions.assertThat;
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

        assertThat( sortFields( sortBuilders ) ).containsExactly( "myfield._number", "myfield._orderby" );
        assertThat( toJson( sortBuilders.get( 0 ) ) ).contains( "\"order\":\"asc\"", "\"missing\":\"_last\"",
                                                                 "\"unmapped_type\":\"double\"" );
    }

    @Test
    void createFieldWithoutDirectionSort()
    {
        final Set<OrderExpr> orderExprs = new HashSet<>();
        orderExprs.add( new FieldOrderExpr( FieldExpr.from( "myField" ), null ) );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( orderExprs );

        assertThat( sortFields( sortBuilders ) ).containsExactly( "myfield._number", "myfield._orderby" );
        assertThat( toJson( sortBuilders.get( 0 ) ) ).doesNotContain( "\"order\"" );
    }

    @Test
    void createMultipleFieldSort()
    {
        final List<OrderExpr> orderExprs = List.of( new FieldOrderExpr( FieldExpr.from( "myField" ), OrderExpr.Direction.ASC ),
                                                    new FieldOrderExpr( FieldExpr.from( "mySecondField" ), OrderExpr.Direction.DESC ) );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( orderExprs );

        assertThat( sortFields( sortBuilders ) ).containsExactly( "myfield._number", "myfield._orderby", "mysecondfield._number",
                                                                  "mysecondfield._orderby" );
    }

    @Test
    void createFieldSortWithLanguage()
    {
        final FieldOrderExpr orderExpr =
            FieldOrderExpr.create( IndexPath.from( "myField" ), OrderExpr.Direction.ASC, Locale.forLanguageTag( "no" ) );

        final List<SortBuilder> sortBuilders =
            new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( List.of( orderExpr ) );

        // Numeric value, then collation of the language, then binary order for documents indexed without that language
        assertThat( sortFields( sortBuilders ) ).containsExactly( "myfield._number", languageOrderByField(), "myfield._orderby" );
    }

    @Test
    void languageFallbackGoesRightAfterItsLanguageSort()
    {
        final FieldOrderExpr languageOrderExpr =
            FieldOrderExpr.create( IndexPath.from( "myField" ), OrderExpr.Direction.ASC, Locale.forLanguageTag( "no" ) );
        final FieldOrderExpr secondaryOrderExpr = new FieldOrderExpr( FieldExpr.from( "priority" ), OrderExpr.Direction.ASC );

        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create(
            List.of( languageOrderExpr, secondaryOrderExpr ) );

        assertThat( sortFields( sortBuilders ) ).containsExactly( "myfield._number", languageOrderByField(), "myfield._orderby",
                                                                  "priority._number", "priority._orderby" );
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

        assertThat( sortFields( sortBuilders ) ).containsExactly( "myfield._number", languageOrderByField(), "myfield._orderby" );
        assertThat( toJson( sortBuilders.get( 0 ) ) ).contains( "\"order\":\"desc\"" );
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

        // _name only ever holds strings, so it gets no numeric sort
        assertThat( sortBuilders.subList( 0, 3 ) ).allMatch( GeoDistanceSortBuilder.class::isInstance );
        assertThat( sortFields( sortBuilders.subList( 3, sortBuilders.size() ) ) ).containsExactly( "myfield._number", "myfield._orderby",
                                                                                                    "_name._orderby" );
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

    @ParameterizedTest
    @ValueSource(strings = {"_score", "_id", "_doc", "_ts", "_versionKey", "_nodeType", "_manualOrderValue", "_path", "_name",
        "_parentPath"})
    void singleTypeFieldsGetNoNumberSort( final String field )
    {
        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create(
            List.of( new FieldOrderExpr( FieldExpr.from( field ), OrderExpr.Direction.ASC ) ) );

        assertThat( sortFields( sortBuilders ) ).containsExactly(
            SearchQueryFieldNameResolver.INSTANCE.resolveOrderByFieldName( IndexPath.from( field ), null ) );
    }

    @Test
    void storageIndexSortGetsNoNumberSort()
    {
        final List<SortBuilder> sortBuilders = new SortQueryBuilderFactory( StoreQueryFieldNameResolver.INSTANCE ).create(
            List.of( new FieldOrderExpr( FieldExpr.from( "myField" ), OrderExpr.Direction.ASC ) ) );

        assertThat( sortFields( sortBuilders ) ).containsExactly( "myfield" );
    }

    private static String languageOrderByField()
    {
        return SearchQueryFieldNameResolver.INSTANCE.resolveOrderByFieldName( IndexPath.from( "myField" ), Locale.forLanguageTag( "no" ) );
    }

    private static List<String> sortFields( final List<SortBuilder> sortBuilders )
    {
        return sortBuilders.stream().map( SortBuilderFactoryTest::sortField ).toList();
    }

    private static String sortField( final SortBuilder sortBuilder )
    {
        final String json = toJson( sortBuilder );
        return json.substring( 2, json.indexOf( '"', 2 ) );
    }

    private static String toJson( final SortBuilder sortBuilder )
    {
        try
        {
            final XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
            sortBuilder.toXContent( builder, ToXContent.EMPTY_PARAMS );
            return builder.endObject().string();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
