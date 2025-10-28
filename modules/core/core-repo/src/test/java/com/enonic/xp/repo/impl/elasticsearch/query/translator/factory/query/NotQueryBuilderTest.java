package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotQueryBuilderTest
    extends BaseTestBuilderFactory
{

    @Test
    void negate_term()
    {
        final String expected = load( "not_term.json" );

        final CompareExpr compareExpr = CompareExpr.create( FieldExpr.from( "fisk" ), CompareExpr.Operator.EQ, ValueExpr.string( "ost" ) );
        final QueryBuilder query =
            NotQueryBuilder.build( CompareExpressionBuilder.build( compareExpr, SearchQueryFieldNameResolver.INSTANCE ) );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}
