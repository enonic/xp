package com.enonic.wem.query.expr

import spock.lang.Specification

class FunctionExprTest extends Specification
{
    def "test function expression"( )
    {
        given:
        def arg = ValueExpr.string( "arg1" )
        def expr = new FunctionExpr( "name", [arg] )

        expect:
        expr.getName() == "name"
        expr.getArguments() != null
        expr.getArguments().size() == 1
        expr.getArguments()[0] == arg
        expr.toString() == "name('arg1')"
    }
}
