package com.enonic.wem.core.index.query

import com.enonic.wem.api.query.expr.CompareExpr
import com.enonic.wem.api.query.expr.FieldExpr
import com.enonic.wem.api.query.expr.ValueExpr
import spock.lang.Specification
import spock.lang.Unroll

class IndexQueryFieldNameResolverTest
    extends Specification
{
    @Unroll
    def "given path '#field' and valuetype #valueExp.value.type then expect fieldname '#resolvedFieldName'"()
    {
        expect:
        resolvedFieldName == IndexQueryFieldNameResolver.resolve( CompareExpr.eq( new FieldExpr( field ), valueExp ) )

        where:
        field   | valueExp                                        | resolvedFieldName
        "A"     | ValueExpr.string( "test" )                      | "a"
        "A.b"   | ValueExpr.string( "test" )                      | "a_b"
        "A.B.c" | ValueExpr.string( "test" )                      | "a_b_c"
        "A.b.c" | ValueExpr.number( 1.0 )                         | "a_b_c._number"
        "A.B.C" | ValueExpr.number( 1L )                          | "a_b_c._number"
        "A.B.C" | ValueExpr.geoPoint( "80,80" )                   | "a_b_c._geopoint"
        "A.B.C" | ValueExpr.instant( "2013-08-01T10:00:00.000Z" ) | "a_b_c._datetime"
    }
}