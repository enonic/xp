package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TermExpressionBuilderTest
    extends BaseTestBuilderFactory
{
    @Test
    void compare_eq_string()
    {
        final String expected = load( "compare_eq_string.json" );

        final QueryBuilder query =
            TermExpressionBuilder.build( CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.string( "myValue" ) ),
                                         SearchQueryFieldNameResolver.INSTANCE );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    void compare_eq_number()
    {
        final String expected = load( "compare_eq_number.json" );

        final QueryBuilder query = TermExpressionBuilder.build( CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.number( 1 ) ),
                                                                SearchQueryFieldNameResolver.INSTANCE );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    void compare_eq_datetime()
    {
        final String expected = load( "compare_eq_datetime.json" );

        final QueryBuilder query =
            TermExpressionBuilder.build( CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.instant( "2013-11-29T09:42:00Z" ) ),
                                         SearchQueryFieldNameResolver.INSTANCE );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    void compare_eq_geopoint()
    {
        final String expected = load( "compare_eq_geopoint.json" );

        final QueryBuilder query =
            TermExpressionBuilder.build( CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.geoPoint( "59.9127300,10.746090" ) ),
                                         SearchQueryFieldNameResolver.INSTANCE );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}
