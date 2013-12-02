package com.enonic.wem.core.index.query

import com.enonic.wem.api.data.Value
import com.enonic.wem.query.parser.QueryParser
import com.enonic.wem.query.queryfilter.QueryFilter
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Sets
import spock.lang.Ignore
import spock.lang.Unroll

class QueryBuilderFactoryTest extends BaseTestQueryBuilderFactory
{
    @Unroll
    def "create query #query"( )
    {
        given:
        def QueryBuilderFactory factory = new QueryBuilderFactory()

        expect:
        def expected = this.getClass().getResource( fileName ).text
        def expression = factory.create( query, null ).toString()

        cleanString( expected ) == cleanString( expression )

        where:
        fileName                        | query
        "not_range.json"                | QueryParser.parse( "not( myField > 1) " )
        "not_not_range.json"            | QueryParser.parse( "not( not( myField > 1  ))" )
        "function/fulltext_3_args.json" | QueryParser.parse( "fulltext('myField', 'my search phrase', 'OR')" )
    }

    def "create query with queryfilter"( )
    {
        given:
        QueryBuilderFactory factory = new QueryBuilderFactory();
        def expected = this.getClass().getResource( "query_with_queryfilter.json" ).text

        def queryFilter = QueryFilter.newValueQueryFilter().
                fieldName( "myField" ).
                add( new Value.String( "myValue" ) ).
                add( new Value.String( "mySecondValue" ) ).
                build()

        Set<QueryFilter> queryFilters = Sets.newHashSet();
        queryFilters.add( queryFilter );

        def query = QueryParser.parse( "not( myField > 1) " )

        when:
        def builtQuery = factory.create( query, ImmutableSet.copyOf( queryFilters ) )

        then:
        cleanString( expected ) == cleanString( builtQuery.toString() )
    }


    @Ignore // Since the order of the two filters are random goddamnit!
    def "create query with two queryfilters"( )
    {
        given:
        QueryBuilderFactory factory = new QueryBuilderFactory();
        def expected = this.getClass().getResource( "query_with_2_queryfilters.json" ).text

        def queryFilter1 = QueryFilter.newValueQueryFilter().
                fieldName( "myField" ).
                add( new Value.String( "myValue" ) ).
                add( new Value.String( "mySecondValue" ) ).
                build()

        def queryFilter2 = QueryFilter.newExistsFilter( "myField" )

        Set<QueryFilter> queryFilters = Sets.newHashSet();
        queryFilters.add( queryFilter1 );
        queryFilters.add( queryFilter2 );

        def query = QueryParser.parse( "not( myField > 1) " )

        when:
        def builtQuery = factory.create( query, ImmutableSet.copyOf( queryFilters ) )

        then:

        cleanString( expected ) == cleanString( builtQuery.toString() )
    }


}
