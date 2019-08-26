package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Lists;

import com.enonic.xp.query.expr.ValueExpr;

public class GeoDistanceSortFunctionArgumentsTest
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void argumentsRead()
    {
        final GeoDistanceSortFunctionArguments arguments =
            new GeoDistanceSortFunctionArguments( Lists.newArrayList( ValueExpr.string( "myField" ), ValueExpr.string( "79,80" ) ) );

        assertEquals( "myField", arguments.getFieldName() );
        assertEquals( "geoDistance", arguments.getFunctionName() );
        assertEquals( 79, arguments.getLatitude(), 0 );
        assertEquals( 80, arguments.getLongitude(), 0 );
    }

    @Test
    public void illegalGeoPosition()
    {
        this.exception.expect( FunctionQueryBuilderException.class );
        this.exception.expectMessage( "Illegal argument '179, 80' in function 'geoDistance', position 2" );

        new GeoDistanceSortFunctionArguments( Lists.newArrayList( ValueExpr.string( "myField" ), ValueExpr.string( "179, 80" ) ) );
    }
}
