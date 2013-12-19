package com.enonic.wem.core.index.query

import com.enonic.wem.api.data.Value
import com.enonic.wem.api.query.EntityQuery
import com.enonic.wem.api.query.facet.FacetQuery
import com.enonic.wem.api.query.filter.Filter
import com.enonic.wem.api.query.parser.QueryParser
import com.enonic.wem.core.index.Index
import com.enonic.wem.core.index.IndexType
import org.elasticsearch.index.query.MatchAllQueryBuilder
import org.elasticsearch.index.query.RangeQueryBuilder
import org.elasticsearch.index.query.TermsFilterBuilder
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.GeoDistanceSortBuilder
import spock.lang.Ignore

class EntityQueryTranslatorTest
        extends BaseTestBuilderFactory
{
    def "query values populated"()
    {
        given:
        EntityQueryTranslator entityQueryTranslator = new EntityQueryTranslator()
        EntityQuery entityQuery = EntityQuery.newQuery().query( QueryParser.parse( "myField >= 1" ) ).build()

        when:
        def translatedQuery = entityQueryTranslator.translate( entityQuery )

        then:
        translatedQuery.getQuery() != null
        translatedQuery.getQuery() instanceof RangeQueryBuilder
        translatedQuery.getIndex() != null && translatedQuery.getIndex().equals( Index.NODB )
        translatedQuery.getIndexType() != null && translatedQuery.getIndexType().equals( IndexType.NODE )
        translatedQuery.getFilter() == null
        translatedQuery.getFacetBuilders().isEmpty()
    }

    def "filter values populated"()
    {
        given:
        EntityQueryTranslator entityQueryTranslator = new EntityQueryTranslator();

        def queryFilter = Filter.newValueQueryFilter().
                fieldName( "myField" ).
                add( new Value.String( "myValue" ) ).
                add( new Value.String( "mySecondValue" ) ).
                build()

        EntityQuery entityQuery = EntityQuery.newQuery().addFilter( queryFilter ).build();

        when:
        def translatedQuery = entityQueryTranslator.translate( entityQuery )

        then:
        translatedQuery.getQuery() instanceof MatchAllQueryBuilder
        translatedQuery.getIndex() != null && translatedQuery.getIndex().equals( Index.NODB )
        translatedQuery.getIndexType() != null && translatedQuery.getIndexType().equals( IndexType.NODE )
        translatedQuery.getFilter() != null
        translatedQuery.getFilter() instanceof TermsFilterBuilder
        translatedQuery.getFacetBuilders().isEmpty()
    }

    def "sort values populated"()
    {
        given:
        EntityQueryTranslator entityQueryTranslator = new EntityQueryTranslator();
        EntityQuery entityQuery = EntityQuery.newQuery().query( QueryParser.parse( "myField >= 1 ORDER BY myField DESC" ) ).build();

        when:
        def translatedQuery = entityQueryTranslator.translate( entityQuery )

        then:
        translatedQuery.getIndex() != null && translatedQuery.getIndex().equals( Index.NODB );
        translatedQuery.getIndexType() != null && translatedQuery.getIndexType().equals( IndexType.NODE );
        translatedQuery.getSortBuilders() != null;
        translatedQuery.getSortBuilders().size() == 1;
        translatedQuery.getSortBuilders().iterator().next() instanceof FieldSortBuilder
    }


    def "sort value function geoDistance"()
    {
        given:
        EntityQueryTranslator entityQueryTranslator = new EntityQueryTranslator();
        EntityQuery entityQuery = EntityQuery.newQuery().query(
                QueryParser.parse( "myField >= 1 ORDER BY geoDistance('myField', '-70,-50') ASC" ) ).build();

        when:
        def translatedQuery = entityQueryTranslator.translate( entityQuery )

        then:
        translatedQuery.getIndex() != null && translatedQuery.getIndex().equals( Index.NODB );
        translatedQuery.getIndexType() != null && translatedQuery.getIndexType().equals( IndexType.NODE );
        translatedQuery.getSortBuilders() != null;
        translatedQuery.getSortBuilders().size() == 1;
        translatedQuery.getSortBuilders().iterator().next() instanceof GeoDistanceSortBuilder
    }

    def "match all with termsfacet"()
    {
        given:
        def expected = this.getClass().getResource( "match_all_with_termsfacet.json" ).text
        EntityQueryTranslator entityQueryTranslator = new EntityQueryTranslator();
        EntityQuery entityQuery = EntityQuery.newQuery().
                addFacet( FacetQuery.newTermsFacetQuery( "myTermsFacet" ).fields( ["myField"] ).build() ).
                build();

        when:
        def translatedQuery = entityQueryTranslator.translate( entityQuery )

        then:
        def String translatedQueryString = translatedQuery.toSearchSourceBuilder().toString()
        cleanString( expected ) == cleanString( translatedQueryString )

    }


    @Ignore // Because of changes in order of stuff.
    def "big ugly query containing everything"()
    {
        given:
        def expected = this.getClass().getResource( "big_ugly_do_it_all_query.json" ).text
        def EntityQueryTranslator entityQueryTranslator = new EntityQueryTranslator();
        def EntityQuery.Builder builder = EntityQuery.
                newQuery().
                query( QueryParser.parse(
                        "myField >= 1 AND fulltext('myField', 'myPhrase', 'OR') ORDER BY geoDistance('myField', '-70,-50') ASC, myField DESC" ) )

        builder.addFilter( Filter.newValueQueryFilter().
                                   fieldName( "myField" ).
                                   add( new Value.String( "myValue" ) ).
                                   add( new Value.String( "mySecondValue" ) ).
                                   build() );

        builder.addQueryFilter( Filter.newExistsFilter( "doesThisFieldExist" ) );

        builder.addFacet( FacetQuery.newTermsFacetQuery( "myTermFacet" ).fields( ["myTermField"] ).build() );

        when:
        def translatedQuery = entityQueryTranslator.translate( builder.build() )

        then:
        def String translatedQueryString = translatedQuery.toSearchSourceBuilder().toString()
        //println translatedQueryString;
        cleanString( expected ) == cleanString( translatedQueryString )
    }
}
