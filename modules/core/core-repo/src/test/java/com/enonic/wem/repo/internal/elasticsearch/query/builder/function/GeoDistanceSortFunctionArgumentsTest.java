package com.enonic.wem.repo.internal.elasticsearch.query.builder.function;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Lists;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.wem.repo.internal.elasticsearch.function.FunctionQueryBuilderException;
import com.enonic.wem.repo.internal.elasticsearch.function.GeoDistanceSortFunctionArguments;

public class GeoDistanceSortFunctionArgumentsTest
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void argumentsRead()
    {
        final GeoDistanceSortFunctionArguments arguments =
            new GeoDistanceSortFunctionArguments( Lists.newArrayList( ValueExpr.string( "myField" ), ValueExpr.string( "79,80" ) ) );

        Assert.assertEquals( "myField", arguments.getFieldName() );
        Assert.assertEquals( "geoDistance", arguments.getFunctionName() );
        Assert.assertEquals( 79, arguments.getLatitude(), 0 );
        Assert.assertEquals( 80, arguments.getLongitude(), 0 );
    }

    @Test
    public void illegalGeoPosition()
    {
        this.exception.expect( FunctionQueryBuilderException.class );
        this.exception.expectMessage( "Illegal argument '179, 80' in function 'geoDistance', positon 2" );

        new GeoDistanceSortFunctionArguments( Lists.newArrayList( ValueExpr.string( "myField" ), ValueExpr.string( "179, 80" ) ) );
    }
}
