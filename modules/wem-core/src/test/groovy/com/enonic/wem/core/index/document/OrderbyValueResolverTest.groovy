package com.enonic.wem.core.index.document

import com.enonic.wem.api.data.Value
import com.google.common.collect.Lists
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class OrderbyValueResolverTest extends Specification
{

    def "orderby for null-value"( )
    {
        when:
        def orderByValue = OrderbyValueResolver.getOrderbyValue( null );

        then:
        orderByValue == null
    }

    def "orderBy-values with #comment is string-sortable"( )
    {
        expect:
        valueList.sort() == sortedList

        where:
        comment    | valueList                                                                  | sortedList
        "string"   | createOrderByValues( "b", "a", "d", "c" )                                  | createOrderByValues( "a", "b", "c", "d" )
        "double"   | createOrderByValues( 12, 0, 100, 10, 2 )                                   | createOrderByValues( 0, 2, 10, 12, 100 )
        "datetime" | createDateTimeOrderByValue( "2001-01-02T00:00:00", "2001-01-01T00:00:00" ) | createDateTimeOrderByValue( "2001-01-01T00:00:00", "2001-01-02T00:00:00" )
    }

    def createDateTimeOrderByValue( String... values )
    {
        def unsorted = Lists.newArrayList()
        values.each {
            value -> unsorted.add( OrderbyValueResolver.getOrderbyValue( new Value.DateTime( value ) ) ); }
        return unsorted
    }

    def createOrderByValues( Double... values )
    {
        def unsorted = Lists.newArrayList()
        values.each {
            value -> unsorted.add( OrderbyValueResolver.getOrderbyValue( new Value.Double( value ) ) ); }
        return unsorted
    }

    def createOrderByValues( String... values )
    {
        def unsorted = Lists.newArrayList()
        values.each {
            value -> unsorted.add( OrderbyValueResolver.getOrderbyValue( new Value.String( value ) ) ); }
        return unsorted
    }

}
