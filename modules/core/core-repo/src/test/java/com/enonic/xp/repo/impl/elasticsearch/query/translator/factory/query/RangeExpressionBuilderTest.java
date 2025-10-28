package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RangeExpressionBuilderTest
    extends BaseTestBuilderFactory
{

    @Test
    void compare_gt_number()
    {
        final String expected = load( "compare_gt_number.json" );

        final QueryBuilder query = RangeExpressionBuilder.build( CompareExpr.gt( FieldExpr.from( "myField" ), ValueExpr.number( 3L ) ),
                                                                 SearchQueryFieldNameResolver.INSTANCE );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }


    @Test
    void compare_lt_number()
    {
        final String expected = load( "compare_lt_number.json" );

        final QueryBuilder query = RangeExpressionBuilder.build( CompareExpr.lt( FieldExpr.from( "myField" ), ValueExpr.number( 3L ) ),
                                                                 SearchQueryFieldNameResolver.INSTANCE );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    void compare_gte_number()
    {
        final String expected = load( "compare_gte_number.json" );

        final QueryBuilder query = RangeExpressionBuilder.build( CompareExpr.gte( FieldExpr.from( "myField" ), ValueExpr.number( 3L ) ),
                                                                 SearchQueryFieldNameResolver.INSTANCE );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    void compare_gte_instant()
    {
        final String expected = load( "compare_gt_datetime.json" );

        final QueryBuilder query =
            RangeExpressionBuilder.build( CompareExpr.gt( FieldExpr.from( "myField" ), ValueExpr.instant( "2013-11-29T11:00:00.000Z" ) ),
                                          SearchQueryFieldNameResolver.INSTANCE );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }

    @Test
    void compare_gt_string()
    {
        final String expected = load( "compare_gt_string.json" );

        final QueryBuilder query =
            RangeExpressionBuilder.build( CompareExpr.gt( FieldExpr.from( "myField" ), ValueExpr.string( "myString" ) ),
                                          SearchQueryFieldNameResolver.INSTANCE );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}

