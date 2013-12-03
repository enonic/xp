package com.enonic.wem.core.index.query.function

import com.enonic.wem.query.expr.ValueExpr
import spock.lang.Specification

class GeoDistanceSortFunctionArgumentsTest extends Specification
{

    def "arguments read"( )
    {
        when:
        GeoDistanceSortFunctionArguments arguments = new GeoDistanceSortFunctionArguments( [ValueExpr.string( "myField" ), ValueExpr.string( "79,80" )] )

        then:
        arguments.fieldName == "myField"
        arguments.functionName == "geoDistance"
        arguments.latitude == 79
        arguments.longitude == 80
    }

    def "illegal geo-position"( )
    {
        when:
        GeoDistanceSortFunctionArguments arguments = new GeoDistanceSortFunctionArguments( [ValueExpr.string( "myField" ), ValueExpr.string( "179, 80" )] )

        then:
        def exception = thrown( FunctionQueryBuilderException )
        exception.message == 'Illegal argument \'179, 80\' in function \'geoDistance\', positon 2'
        exception.getCause().message == "Invalid value: latitude not within range from -90.0 to 90.0: 179, 80"
    }


}
