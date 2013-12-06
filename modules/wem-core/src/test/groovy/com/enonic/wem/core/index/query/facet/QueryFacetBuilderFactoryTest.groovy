package com.enonic.wem.core.index.query.facet

import com.enonic.wem.core.index.query.BaseTestBuilderFactory
import com.enonic.wem.query.facet.FacetQuery
import com.enonic.wem.query.filter.Filter
import com.enonic.wem.query.parser.QueryParser
import org.elasticsearch.search.facet.query.QueryFacetBuilder

class QueryFacetBuilderFactoryTest
        extends BaseTestBuilderFactory
{
    def "create minimal queryfacet"()
    {
        given:
        def queryFacet = FacetQuery.
                newQueryFacetQuery( "myQueryFacet" ).
                query( QueryParser.parse( "myField = 1" ) ).
                build()
        def expected = this.getClass().getResource( "minimal_queryfacet.json" ).text
        def QueryFacetBuilderFactory factory = new QueryFacetBuilderFactory()

        when:
        def QueryFacetBuilder queryFacetBuilder = factory.create( queryFacet )

        then:
        cleanString( expected ) == cleanString( getJson( queryFacetBuilder ) );
    }


    def "create maximized queryfacet"()
    {
        given:
        def queryFacet = FacetQuery.
                newQueryFacetQuery( "myQueryFacet" ).
                query( QueryParser.parse( "myField > 1" ) ).
                filter( Filter.newExistsFilter( "myFieldThatShouldExist" ) ).
                build()
        def expected = this.getClass().getResource( "maximized_queryfacet.json" ).text
        def QueryFacetBuilderFactory factory = new QueryFacetBuilderFactory()

        when:
        def QueryFacetBuilder queryFacetBuilder = factory.create( queryFacet )

        then:
        cleanString( expected ) == cleanString( getJson( queryFacetBuilder ) );
    }


}
