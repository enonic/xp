package com.enonic.wem.repo.internal.elasticsearch.query.builder

import com.enonic.wem.api.data2.Value
import com.enonic.wem.api.query.filter.ValueFilter
import com.enonic.wem.api.query.parser.QueryParser
import spock.lang.Unroll

class QueryBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    @Unroll
    def "create query #query"()
    {
        given:
        def QueryBuilderFactory queryBuilderFactory = new QueryBuilderFactory()
        def expected = this.getClass().getResource( fileContainingExpectedJson ).text
        def expression = queryBuilderFactory.
            create().
            queryExpr( QueryParser.parse( query ) ).
            build().
            toString()

        def expectedJson = cleanString( expected )
        def actualJson = cleanString( expression )

        expect:
        expectedJson == actualJson

        where:
        query                                           | fileContainingExpectedJson
        "not( myField > 1) "                            | "not_range.json"
        "not( not( myField > 1  ))"                     | "not_not_range.json"
        "fulltext('myField', 'my search phrase', 'OR')" | "fulltext_3_args.json"
    }

    def "create query with queryfilter"()
    {
        given:
        QueryBuilderFactory factory = new QueryBuilderFactory();
        def expected = this.getClass().getResource( "query_with_queryfilter.json" ).text

        def queryFilter = ValueFilter.create().
            fieldName( "myField" ).
            addValue( Value.newString( "myValue1" ) ).
            addValue( Value.newString( "myValue2" ) ).
            build()

        def query = QueryParser.parse( "not( myField > 1) " )

        when:
        def builtQuery = factory.create().
            queryExpr( query ).
            addQueryFilter( queryFilter ).
            build();

        then:
        cleanString( expected ) == cleanString( builtQuery.toString() )
    }
}
