package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.query.expr.ValueExpr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeoDistanceSortFunctionArgumentsTest
{
    @Test
    void argumentsRead()
    {
        final GeoDistanceSortFunctionArguments arguments =
            new GeoDistanceSortFunctionArguments( List.of( ValueExpr.string( "myField" ), ValueExpr.string( "79,80" ), ValueExpr.string( "km" ) ) );

        assertEquals( "myField", arguments.getFieldName() );
        assertEquals( "geoDistance", arguments.getFunctionName() );
        assertEquals( 79, arguments.getLatitude(), 0 );
        assertEquals( 80, arguments.getLongitude(), 0 );
        assertEquals( "km", arguments.getUnit() );
    }

    @Test
    void illegalGeoPosition()
    {
        final FunctionQueryBuilderException ex = assertThrows( FunctionQueryBuilderException.class,
                                                               () -> new GeoDistanceSortFunctionArguments(
                                                                   List.of( ValueExpr.string( "myField" ),
                                                                            ValueExpr.string( "179, 80" ) ) ) );
        assertEquals( "Illegal argument '179, 80' in function 'geoDistance', position 2", ex.getMessage() );
    }
}
