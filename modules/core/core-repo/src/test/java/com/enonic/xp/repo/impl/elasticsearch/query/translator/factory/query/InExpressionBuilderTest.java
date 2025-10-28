package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InExpressionBuilderTest
    extends BaseTestBuilderFactory
{
    @Test
    void compareInString()
    {
        final String expected = load( "compare_in_string.json" );

        final QueryBuilder query = InExpressionBuilder.build( CompareExpr.in( FieldExpr.from( "myField" ),
                                                                              List.of( ValueExpr.string( "myFirstValue" ),
                                                                                       ValueExpr.string( "mySecondValue" ) ) ),
                                                              SearchQueryFieldNameResolver.INSTANCE );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}
