package com.enonic.wem.repo.internal.elasticsearch.query.builder

import com.enonic.wem.api.query.expr.CompareExpr
import com.enonic.wem.api.query.expr.FieldExpr
import com.enonic.wem.api.query.expr.ValueExpr
import spock.lang.Unroll

class CompareQueryFactoryTest
    extends BaseTestBuilderFactory
{
    @Unroll
    def "build #compareExpr"()
    {
        given:
        def CompareQueryBuilderFactory builder = new CompareQueryBuilderFactory();


        expect:
        def expected = this.getClass().getResource( fileName ).text
        def expression = builder.create( compareExpr ).toString()

        cleanString( expected ) == cleanString( expression )


        where:
        fileName                       | compareExpr
        "compare_eq_string.json"       | CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.string( "myValue" ) )
        "compare_eq_number.json"       | CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.number( 1.0 ) )
        "compare_eq_datetime.json"     | CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.instant( "2013-11-29T09:42:00.000Z" ) )
        "compare_eq_geopoint.json"     | CompareExpr.eq( FieldExpr.from( "myField" ), ValueExpr.geoPoint( "59.9127300,10.746090" ) )
        "compare_neq_string.json"      | CompareExpr.neq( FieldExpr.from( "myField" ), ValueExpr.string( "myValue" ) )
        "compare_not_like_string.json" | CompareExpr.notLike( FieldExpr.from( "myField" ), ValueExpr.string( "myValue" ) )
        "compare_not_in_string.json"   |
            CompareExpr.notIn( FieldExpr.from( "myField" ), [ValueExpr.string( "myFirstValue" ), ValueExpr.string( "mySecondValue" )] );
    }

}


