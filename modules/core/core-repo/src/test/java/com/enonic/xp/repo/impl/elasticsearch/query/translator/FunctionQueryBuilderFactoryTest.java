package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.FunctionQueryBuilderFactory;

import static org.junit.Assert.*;

public class FunctionQueryBuilderFactoryTest
{
    @Test
    public void test_fulltext()
    {
        List<ValueExpr> arguments =
            Lists.newArrayList( ValueExpr.string( "myField" ), ValueExpr.string( "mySearchString" ), ValueExpr.string( "OR" ) );

        final QueryBuilder fulltext = FunctionQueryBuilderFactory.create( new FunctionExpr( "fulltext", arguments ) );

        assertTrue( fulltext instanceof SimpleQueryStringBuilder );
    }
}
