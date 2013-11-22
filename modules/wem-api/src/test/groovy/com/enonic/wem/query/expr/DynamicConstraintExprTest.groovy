package com.enonic.wem.query.expr

import spock.lang.Specification

class DynamicConstraintExprTest extends Specification
{
    def "test dynamic constraint expression"( )
    {
        given:
        def func = new FunctionExpr( "name", [] )
        def expr = new DynamicConstraintExpr( func )

        expect:
        expr.getFunction() == func
        expr.toString() == "name()"
    }
}
