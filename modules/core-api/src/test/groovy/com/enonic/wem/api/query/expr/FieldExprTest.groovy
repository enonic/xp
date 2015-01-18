package com.enonic.wem.api.query.expr

import spock.lang.Specification

class FieldExprTest
        extends Specification
{
    def "test field expression"()
    {
        given:
        def expr = FieldExpr.from( "name" )

        expect:
        expr.getFieldPath() == "name"
        expr.toString() == "name"
    }
}
