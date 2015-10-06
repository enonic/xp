package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.InQueryBuilderFactory;

public class InQueryBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    @Test
    public void compareInString()
        throws Exception
    {
        final String expected = load( "compare_in_string.json" );

        final QueryBuilder query = new InQueryBuilderFactory( new SearchQueryFieldNameResolver() ).create(
            CompareExpr.in( FieldExpr.from( "myField" ),
                            Lists.newArrayList( ValueExpr.string( "myFirstValue" ), ValueExpr.string( "mySecondValue" ) ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}
