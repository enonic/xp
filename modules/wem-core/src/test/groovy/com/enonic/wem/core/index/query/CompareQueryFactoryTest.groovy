package com.enonic.wem.core.index.query

import com.enonic.wem.query.expr.CompareExpr
import com.enonic.wem.query.expr.FieldExpr
import com.enonic.wem.query.expr.ValueExpr
import spock.lang.Unroll

class CompareQueryFactoryTest extends BaseTestQueryBuilderFactory
{
    @Unroll
    def "build #compareExpr"( )
    {
        given:
        def CompareQueryFactory builder = new CompareQueryFactory();


        expect:
        def expected = this.getClass().getResource( fileName ).text
        def expression = builder.create( compareExpr ).toString()

        cleanString( expected ) == cleanString( expression )


        where:
        fileName                       | compareExpr
        "compare_eq_string.json"       | CompareExpr.eq( new FieldExpr( "myField" ), ValueExpr.string( "myValue" ) )
        "compare_eq_number.json"       | CompareExpr.eq( new FieldExpr( "myField" ), ValueExpr.number( 1.0 ) )
        "compare_eq_datetime.json"     | CompareExpr.eq( new FieldExpr( "myField" ), ValueExpr.dateTime( "2013-11-29T09:42:00" ) )
        "compare_eq_geopoint.json"     | CompareExpr.eq( new FieldExpr( "myField" ), ValueExpr.geoPoint( "59.9127300,10.746090" ) )
        "compare_neq_string.json"      | CompareExpr.neq( new FieldExpr( "myField" ), ValueExpr.string( "myValue" ) )
        "compare_not_like_string.json" | CompareExpr.notLike( new FieldExpr( "myField" ), ValueExpr.string( "myValue" ) )
        "compare_not_in_string.json"   | CompareExpr.notIn( new FieldExpr( "myField" ), [ValueExpr.string( "myFirstValue" ), ValueExpr.string( "mySecondValue" )] );
    }

}


