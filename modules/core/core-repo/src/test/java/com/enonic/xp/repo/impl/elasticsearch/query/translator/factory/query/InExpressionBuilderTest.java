package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.BaseTestBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;

public class InExpressionBuilderTest
    extends BaseTestBuilderFactory
{
    @Test
    public void compareInString()
        throws Exception
    {
        final String expected = load( "compare_in_string.json" );

        final QueryBuilder query = InExpressionBuilder.build( CompareExpr.in( FieldExpr.from( "myField" ),
                                                                              Lists.newArrayList( ValueExpr.string( "myFirstValue" ),
                                                                                                  ValueExpr.string( "mySecondValue" ) ) ),
                                                              new SearchQueryFieldNameResolver() );

        assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}
