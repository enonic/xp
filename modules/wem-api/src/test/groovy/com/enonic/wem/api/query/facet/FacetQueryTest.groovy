package com.enonic.wem.api.query.facet

import com.enonic.wem.api.query.filter.Filter
import com.enonic.wem.api.query.parser.QueryParser
import spock.lang.Specification

class FacetQueryTest
        extends Specification
{
    def "builder termsfacet"()
    {
        when:
        TermsFacetQuery termsFacetQuery = FacetQuery.newTermsFacetQuery( "myTermsFacet" ).
                allTerms( true ).
                fields( ["myField", "mySecondField"] ).
                build();

        then:
        termsFacetQuery.getName() == "myTermsFacet"
        termsFacetQuery.getFields().size() == 2
        termsFacetQuery.excludes == null || termsFacetQuery.excludes.isEmpty()
    }


    def "builder queryfacet"()
    {
        when:
        QueryFacetQuery queryFacetQuery = FacetQuery.newQueryFacetQuery( "myQueryFacet" ).
                query( QueryParser.parse( "myField = 2" ) ).
                filter( Filter.newExistsFilter( "mySecondField" ) ).
                build();

        then:
        queryFacetQuery.getName() == "myQueryFacet"
        queryFacetQuery.getQuery() != null
        queryFacetQuery.getQueryFilter() != null

    }

}
