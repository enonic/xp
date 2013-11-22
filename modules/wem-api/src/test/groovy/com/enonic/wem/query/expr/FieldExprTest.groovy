package com.enonic.wem.query.expr

import spock.lang.Specification

class FieldExprTest extends Specification
{
    def "test field expression"( )
    {
        given:
        def expr = new FieldExpr( "name" )

        expect:
        expr.getName() == "name"
        expr.toString() == "name"
    }
}
