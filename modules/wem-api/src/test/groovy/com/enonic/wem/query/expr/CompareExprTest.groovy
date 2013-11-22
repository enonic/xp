package com.enonic.wem.query.expr

import spock.lang.Specification

class CompareExprTest extends Specification
{
    def "test EQ compare"( )
    {
        given:
        def field = new FieldExpr( "a" )
        def value = ValueExpr.number( 2 );
        def expr = CompareExpr.eq( field, value )

        expect:
        expr != null
        expr.getField() == field
        expr.getValues() == [value]
        expr.getOperator() == CompareExpr.Operator.EQ
        !expr.getOperator().allowMultipleValues()
        expr.getFirstValue() == value
        expr.toString() == "a = 2.0"
    }

    def "test NEQ compare"( )
    {
        given:
        def field = new FieldExpr( "a" )
        def value = ValueExpr.number( 2 );
        def expr = CompareExpr.neq( field, value )

        expect:
        expr != null
        expr.getField() == field
        expr.getValues() == [value]
        expr.getOperator() == CompareExpr.Operator.NEQ
        !expr.getOperator().allowMultipleValues()
        expr.getFirstValue() == value
        expr.toString() == "a != 2.0"
    }

    def "test GT compare"( )
    {
        given:
        def field = new FieldExpr( "a" )
        def value = ValueExpr.number( 2 );
        def expr = CompareExpr.gt( field, value )

        expect:
        expr != null
        expr.getField() == field
        expr.getValues() == [value]
        expr.getOperator() == CompareExpr.Operator.GT
        !expr.getOperator().allowMultipleValues()
        expr.getFirstValue() == value
        expr.toString() == "a > 2.0"
    }

    def "test GTE compare"( )
    {
        given:
        def field = new FieldExpr( "a" )
        def value = ValueExpr.number( 2 );
        def expr = CompareExpr.gte( field, value )

        expect:
        expr != null
        expr.getField() == field
        expr.getValues() == [value]
        expr.getOperator() == CompareExpr.Operator.GTE
        !expr.getOperator().allowMultipleValues()
        expr.getFirstValue() == value
        expr.toString() == "a >= 2.0"
    }

    def "test LT compare"( )
    {
        given:
        def field = new FieldExpr( "a" )
        def value = ValueExpr.number( 2 );
        def expr = CompareExpr.lt( field, value )

        expect:
        expr != null
        expr.getField() == field
        expr.getValues() == [value]
        expr.getOperator() == CompareExpr.Operator.LT
        !expr.getOperator().allowMultipleValues()
        expr.getFirstValue() == value
        expr.toString() == "a < 2.0"
    }

    def "test LTE compare"( )
    {
        given:
        def field = new FieldExpr( "a" )
        def value = ValueExpr.number( 2 );
        def expr = CompareExpr.lte( field, value )

        expect:
        expr != null
        expr.getField() == field
        expr.getValues() == [value]
        expr.getOperator() == CompareExpr.Operator.LTE
        !expr.getOperator().allowMultipleValues()
        expr.getFirstValue() == value
        expr.toString() == "a <= 2.0"
    }

    def "test LIKE compare"( )
    {
        given:
        def field = new FieldExpr( "a" )
        def value = ValueExpr.string( '2' );
        def expr = CompareExpr.like( field, value )

        expect:
        expr != null
        expr.getField() == field
        expr.getValues() == [value]
        expr.getOperator() == CompareExpr.Operator.LIKE
        !expr.getOperator().allowMultipleValues()
        expr.getFirstValue() == value
        expr.toString() == "a LIKE '2'"
    }

    def "test NOT LIKE compare"( )
    {
        given:
        def field = new FieldExpr( "a" )
        def value = ValueExpr.string( '2' );
        def expr = CompareExpr.notLike( field, value )

        expect:
        expr != null
        expr.getField() == field
        expr.getValues() == [value]
        expr.getOperator() == CompareExpr.Operator.NOT_LIKE
        !expr.getOperator().allowMultipleValues()
        expr.getFirstValue() == value
        expr.toString() == "a NOT LIKE '2'"
    }

    def "test IN compare"( )
    {
        given:
        def field = new FieldExpr( "a" )
        def values = [ValueExpr.string( '1' ), ValueExpr.string( '2' )]
        def expr = CompareExpr.in( field, values )

        expect:
        expr != null
        expr.getField() == field
        expr.getValues() == values
        expr.getOperator() == CompareExpr.Operator.IN
        expr.getOperator().allowMultipleValues()
        expr.getFirstValue() == values[0]
        expr.toString() == "a IN ('1', '2')"
    }

    def "test NOT IN compare"( )
    {
        given:
        def field = new FieldExpr( "a" )
        def values = [ValueExpr.string( '1' ), ValueExpr.string( '2' )]
        def expr = CompareExpr.notIn( field, values )

        expect:
        expr != null
        expr.getField() == field
        expr.getValues() == values
        expr.getOperator() == CompareExpr.Operator.NOT_IN
        expr.getOperator().allowMultipleValues()
        expr.getFirstValue() == values[0]
        expr.toString() == "a NOT IN ('1', '2')"
    }
}
