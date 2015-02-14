package com.enonic.wem.repo.internal.elasticsearch.query.builder;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.core.query.expr.CompareExpr;
import com.enonic.xp.core.query.expr.FieldExpr;
import com.enonic.xp.core.query.expr.ValueExpr;

public class InQueryBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    @Test
    public void compareInString()
        throws Exception
    {
        final String expected = load( "compare_in_string.json" );

        final QueryBuilder query = InQueryBuilderFactory.create( CompareExpr.in( FieldExpr.from( "myField" ),
                                                                                 Lists.newArrayList( ValueExpr.string( "myFirstValue" ),
                                                                                                     ValueExpr.string(
                                                                                                         "mySecondValue" ) ) ) );

        Assert.assertEquals( cleanString( expected ), cleanString( query.toString() ) );
    }
}
