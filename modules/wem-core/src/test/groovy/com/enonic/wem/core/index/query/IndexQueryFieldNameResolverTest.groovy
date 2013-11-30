package com.enonic.wem.core.index.query

import com.enonic.wem.query.expr.CompareExpr
import com.enonic.wem.query.expr.FieldExpr
import com.enonic.wem.query.expr.ValueExpr
import spock.lang.Specification
import spock.lang.Unroll

class IndexQueryFieldNameResolverTest extends Specification
{
    @Unroll
    def "resolve path #field with value of type #valueExp.value.type expect #resolvedFieldName"( )
    {
        expect:
        resolvedFieldName == IndexQueryFieldNameResolver.resolve( CompareExpr.eq( new FieldExpr( field ), valueExp ) )

        where:
        field   | valueExp                                    | resolvedFieldName
        "A"     | ValueExpr.string( "test" )                  | "a"
        "A.b"   | ValueExpr.string( "test" )                  | "a_b"
        "A.B.c" | ValueExpr.string( "test" )                  | "a_b_c"
        "A.b.c" | ValueExpr.number( 1.0 )                     | "a_b_c._number"
        "A.B.C" | ValueExpr.number( 1L )                      | "a_b_c._number"
        "A.B.C" | ValueExpr.geoPoint( "80,80" )               | "a_b_c._geopoint"
        "A.B.C" | ValueExpr.dateTime( "2013-08-01T10:00:00" ) | "a_b_c._datetime"
    }
}
