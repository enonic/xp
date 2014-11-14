package com.enonic.wem.core.elasticsearch.query.builder.function;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.wem.api.query.expr.FunctionExpr;
import com.enonic.wem.api.query.expr.ValueExpr;

import static org.junit.Assert.*;

public class FunctionQueryBuilderFactoryTest
{

    @Test
    public void test_fulltext()
    {
        FunctionQueryBuilderFactory factory = new FunctionQueryBuilderFactory();

        List<ValueExpr> arguments =
            Lists.newArrayList( ValueExpr.string( "myField" ), ValueExpr.string( "mySearchString" ), ValueExpr.string( "OR" ) );

        final QueryBuilder fulltext = factory.create( new FunctionExpr( "fulltext", arguments ) );

        assertTrue( fulltext instanceof SimpleQueryStringBuilder );
    }
}
