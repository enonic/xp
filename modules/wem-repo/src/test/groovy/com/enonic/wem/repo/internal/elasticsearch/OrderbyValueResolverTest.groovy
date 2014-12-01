package com.enonic.wem.repo.internal.elasticsearch

import com.enonic.wem.api.data2.Value
import com.enonic.wem.api.data2.ValueTypes
import com.google.common.collect.Lists
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class OrderbyValueResolverTest
    extends Specification
{

    def "orderby for null-value"()
    {
        when:
        def orderByValue = OrderbyValueResolver.getOrderbyValue( null );

        then:
        orderByValue == null
    }

    def "orderBy-values with #comment is string-sortable"()
    {
        expect:
        valueList.sort() == sortedList

        where:
        comment    | valueList                                                                           | sortedList
        "string"   | createOrderByValues( "b", "a", "d", "c" )                                           |
            createOrderByValues( "a", "b", "c", "d" )
        "double"   | createOrderByValues( 12, 0, 100, 10, 2 )                                            |
            createOrderByValues( 0, 2, 10, 12, 100 )
        "datetime" | createInstantOrderByValue( "2001-01-02T00:00:00.000Z", "2001-01-01T00:00:00.000Z" ) |
            createInstantOrderByValue( "2001-01-01T00:00:00.000Z", "2001-01-02T00:00:00.000Z" )
    }

    def createInstantOrderByValue( String... values )
    {
        def unsorted = Lists.newArrayList()
        values.each { value -> unsorted.add( OrderbyValueResolver.getOrderbyValue( ValueTypes.DATE_TIME.parseValue( value ) ) ); }
        return unsorted
    }

    def createOrderByValues( Double... values )
    {
        def unsorted = Lists.newArrayList()
        values.each { value -> unsorted.add( OrderbyValueResolver.getOrderbyValue( Value.newDouble( value ) ) ); }
        return unsorted
    }

    def createOrderByValues( String... values )
    {
        def unsorted = Lists.newArrayList()
        values.each { value -> unsorted.add( OrderbyValueResolver.getOrderbyValue( Value.newString( value ) ) ); }
        return unsorted
    }

}
