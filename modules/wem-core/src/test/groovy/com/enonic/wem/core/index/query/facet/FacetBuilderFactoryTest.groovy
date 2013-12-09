package com.enonic.wem.core.index.query.facet

import com.enonic.wem.api.query.facet.FacetQuery
import com.enonic.wem.api.query.facet.QueryFacetQuery
import com.enonic.wem.api.query.facet.TermsFacetQuery
import com.enonic.wem.api.query.parser.QueryParser
import com.enonic.wem.core.index.query.BaseTestBuilderFactory
import org.elasticsearch.search.facet.FacetBuilder
import org.elasticsearch.search.facet.query.QueryFacetBuilder
import org.elasticsearch.search.facet.terms.TermsFacetBuilder

class FacetBuilderFactoryTest
        extends BaseTestBuilderFactory
{

    def "create multiple facets"()
    {
        given:
        def FacetBuilderFactory factory = new FacetBuilderFactory();

        def TermsFacetQuery termFacetQuery = FacetQuery.
                newTermsFacetQuery( "myTermsFacet" ).
                fields( ["myField", "mySecondField"] ).
                build();
        def QueryFacetQuery queryFacet = FacetQuery.
                newQueryFacetQuery( "myQueryFacet" ).
                query( QueryParser.parse( "myField = 1" ) ).
                build();

        when:
        Set<FacetBuilder> facetBuilder = factory.create( [termFacetQuery, queryFacet] )

        then:
        facetBuilder.size() == 2

        Iterator<FacetBuilder> iterator = facetBuilder.iterator();

        def queryFacetCreated = false;
        def termsFacetCreated = false;

        while ( iterator.hasNext() )
        {
            FacetBuilder next = iterator.next();

            if ( next instanceof QueryFacetBuilder )
            {
                queryFacetCreated = true;
            }
            else if ( next instanceof TermsFacetBuilder )
            {
                termsFacetCreated = true;
            }
        }

        termsFacetCreated && queryFacetCreated;
    }
}
