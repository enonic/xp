package com.enonic.wem.core.index.query.facet

import com.enonic.wem.core.index.query.BaseTestBuilderFactory
import com.enonic.wem.query.facet.FacetQuery
import com.enonic.wem.query.facet.RegExpFlag
import com.enonic.wem.query.facet.TermsFacetQuery
import org.elasticsearch.search.facet.terms.TermsFacetBuilder

class TermsFacetBuilderFactoryTest
        extends BaseTestBuilderFactory
{

    def "missing fields yields exception"()
    {
        given:
        def TermsFacetBuilderFactory factory = new TermsFacetBuilderFactory();
        def TermsFacetQuery termsFacetQuery = FacetQuery.newTermsFacetQuery( "myTermsFacetQuery" ).build()

        when:
        factory.create( termsFacetQuery )

        then:
        IllegalArgumentException e = thrown( IllegalArgumentException )
        e.getMessage().contains( "missing value 'fields' in facet: 'myTermsFacetQuery'" )

    }

    def "create minimal term-facet"()
    {
        given:
        def TermsFacetBuilderFactory factory = new TermsFacetBuilderFactory();
        def TermsFacetQuery termsFacetQuery = FacetQuery.newTermsFacetQuery( "myTermsFacetQuery" ).
                fields( ["myFirstField"] ).
                build()
        def expected = this.getClass().getResource( "minimal_termfacet.json" ).text

        when:
        def TermsFacetBuilder termsFacetBuilder = factory.create( termsFacetQuery )

        then:
        cleanString( expected ) == cleanString( getJson( termsFacetBuilder ) );
    }

    def "create maximized term-facet"()
    {
        given:
        def TermsFacetBuilderFactory factory = new TermsFacetBuilderFactory();
        def TermsFacetQuery termsFacetQuery = FacetQuery.newTermsFacetQuery( "myTermsFacetQuery" ).
                fields( ["myFirstField", "mySecondField"] ).
                exclude( ["myExludedField", "mySeconExcludedField"] ).
                allTerms( true ).
                regex( "myRegexp" ).
                regexFlags( [RegExpFlag.CASE_INSENSITIVE] ).
                orderBy( TermsFacetQuery.TermFacetOrderBy.REVERSE_COUNT ).
                build()
        def expected = this.getClass().getResource( "maximized_termfacet.json" ).text

        when:
        def TermsFacetBuilder termsFacetBuilder = factory.create( termsFacetQuery )

        then:
        cleanString( expected ) == cleanString( getJson( termsFacetBuilder ) );
    }

}
