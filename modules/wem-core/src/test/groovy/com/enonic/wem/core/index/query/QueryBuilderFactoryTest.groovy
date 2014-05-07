package com.enonic.wem.core.index.query

import com.enonic.wem.api.data.Value
import com.enonic.wem.api.query.filter.Filter
import com.enonic.wem.api.query.parser.QueryParser
import com.enonic.wem.core.index.query.builder.QueryBuilderFactory
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Sets
import spock.lang.Unroll

class QueryBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    @Unroll
    def "create query #query"()
    {
        given:
        def QueryBuilderFactory factory = new QueryBuilderFactory()
        def expected = this.getClass().getResource( fileContainingExpectedJson ).text
        def expression = factory.create( QueryParser.parse( query ), null ).toString()

        def expectedJson = cleanString( expected )
        def actualJson = cleanString( expression )

        expect:
        expectedJson == actualJson

        where:
        query                                           | fileContainingExpectedJson
        "not( myField > 1) "                            | "not_range.json"
        "not( not( myField > 1  ))"                     | "not_not_range.json"
        "fulltext('myField', 'my search phrase', 'OR')" | "function/fulltext_3_args.json"
    }

    def "create query with queryfilter"()
    {
        given:
        QueryBuilderFactory factory = new QueryBuilderFactory();
        def expected = this.getClass().getResource( "query_with_queryfilter.json" ).text

        def queryFilter = Filter.newValueQueryFilter().
            fieldName( "myField" ).
            add( Value.newString( "myValue1" ) ).
            add( Value.newString( "myValue2" ) ).
            build()

        Set<Filter> queryFilters = Sets.newHashSet();
        queryFilters.add( queryFilter );

        def query = QueryParser.parse( "not( myField > 1) " )

        when:
        def builtQuery = factory.create( query, ImmutableSet.copyOf( queryFilters ) )

        then:
        cleanString( expected ) == cleanString( builtQuery.toString() )
    }
}
