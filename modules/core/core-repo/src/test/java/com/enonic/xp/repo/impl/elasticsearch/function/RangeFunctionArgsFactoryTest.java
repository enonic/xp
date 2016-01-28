package com.enonic.xp.repo.impl.elasticsearch.function;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.query.QueryException;
import com.enonic.xp.query.expr.ValueExpr;

public class RangeFunctionArgsFactoryTest
{

    @Test(expected = QueryException.class)
    public void must_be_same_type()
        throws Exception
    {
        List<ValueExpr> args = Lists.newArrayList( ValueExpr.string( "FieldName" ), ValueExpr.string( "LowValue" ),
                                                   ValueExpr.instant( "2015-08-01T10:00:00Z" ) );
        RangeFunctionArgsFactory.create( args );
    }
}