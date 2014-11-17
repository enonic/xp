package com.enonic.wem.repo.internal.elasticsearch.query.builder.function

import com.enonic.wem.api.query.expr.ValueExpr
import com.enonic.wem.repo.internal.elasticsearch.function.FunctionQueryBuilderException
import com.enonic.wem.repo.internal.elasticsearch.function.GeoDistanceSortFunctionArguments
import spock.lang.Specification

class GeoDistanceSortFunctionArgumentsTest
    extends Specification
{

    def "arguments read"()
    {
        when:
        GeoDistanceSortFunctionArguments arguments = new GeoDistanceSortFunctionArguments(
            [ValueExpr.string( "myField" ), ValueExpr.string( "79,80" )] )

        then:
        arguments.fieldName == "myField"
        arguments.functionName == "geoDistance"
        arguments.latitude == 79
        arguments.longitude == 80
    }

    def "illegal geo-position"()
    {
        when:
        new GeoDistanceSortFunctionArguments( [ValueExpr.string( "myField" ), ValueExpr.string( "179, 80" )] )

        then:
        def exception = thrown( FunctionQueryBuilderException )
        exception.message == 'Illegal argument \'179, 80\' in function \'geoDistance\', positon 2'
        exception.getCause().message ==
            "Value of type [java.lang.String] cannot be converted to [GeoPoint]: Latitude [179.0] is not within range [-90.0‥90.0]"
    }
}
