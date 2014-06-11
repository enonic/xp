package com.enonic.wem.core.index.query

import com.enonic.wem.api.data.Value
import com.enonic.wem.api.entity.Workspace
import com.enonic.wem.api.entity.query.EntityQuery
import com.enonic.wem.api.query.filter.Filter
import com.enonic.wem.api.query.parser.QueryParser
import com.enonic.wem.core.index.Index
import com.enonic.wem.core.index.IndexType
import com.enonic.wem.core.index.query.builder.BaseTestBuilderFactory
import org.elasticsearch.index.query.MatchAllQueryBuilder
import org.elasticsearch.index.query.RangeQueryBuilder
import org.elasticsearch.index.query.TermsFilterBuilder
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.GeoDistanceSortBuilder

class EntityQueryTranslatorTest
    extends BaseTestBuilderFactory
{
    def Workspace TEST_WORKSPACE = new Workspace( "test" );

    def "query values populated"()
    {
        given:
        EntityQueryTranslator entityQueryTranslator = new EntityQueryTranslator()
        EntityQuery entityQuery = EntityQuery.newEntityQuery().query( QueryParser.parse( "myField >= 1" ) ).build()

        when:
        def translatedQuery = entityQueryTranslator.translate( entityQuery, TEST_WORKSPACE )

        then:
        translatedQuery.getQuery() != null
        translatedQuery.getQuery() instanceof RangeQueryBuilder
        translatedQuery.getIndexName() != null && translatedQuery.getIndexName().equals( TEST_WORKSPACE.getSearchIndexName() )
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
            add( Value.newString( "myValue" ) ).
            add( Value.newString( "mySecondValue" ) ).
            build()

        EntityQuery entityQuery = EntityQuery.newEntityQuery().addFilter( queryFilter ).build();

        when:
        def translatedQuery = entityQueryTranslator.translate( entityQuery, TEST_WORKSPACE )

        then:
        translatedQuery.getQuery() instanceof MatchAllQueryBuilder
        translatedQuery.getIndexName() != null && translatedQuery.getIndexName().equals( TEST_WORKSPACE.getSearchIndexName() )
        translatedQuery.getIndexType() != null && translatedQuery.getIndexType().equals( IndexType.NODE )
        translatedQuery.getFilter() != null
        translatedQuery.getFilter() instanceof TermsFilterBuilder
        translatedQuery.getFacetBuilders().isEmpty()
    }

    def "sort values populated"()
    {
        given:
        EntityQueryTranslator entityQueryTranslator = new EntityQueryTranslator();
        EntityQuery entityQuery = EntityQuery.newEntityQuery().query( QueryParser.parse( "myField >= 1 ORDER BY myField DESC" ) ).build();

        when:
        def translatedQuery = entityQueryTranslator.translate( entityQuery, TEST_WORKSPACE )

        then:
        translatedQuery.getIndexName() != null && translatedQuery.getIndexName().equals( TEST_WORKSPACE.getSearchIndexName() )
        translatedQuery.getIndexType() != null && translatedQuery.getIndexType().equals( IndexType.NODE );
        translatedQuery.getSortBuilders() != null;
        translatedQuery.getSortBuilders().size() == 1;
        translatedQuery.getSortBuilders().iterator().next() instanceof FieldSortBuilder
    }


    def "sort value function geoDistance"()
    {
        given:
        EntityQueryTranslator entityQueryTranslator = new EntityQueryTranslator();
        EntityQuery entityQuery = EntityQuery.newEntityQuery().query(
            QueryParser.parse( "myField >= 1 ORDER BY geoDistance('myField', '-70,-50') ASC" ) ).build();

        when:
        def translatedQuery = entityQueryTranslator.translate( entityQuery, TEST_WORKSPACE )

        then:
        translatedQuery.getIndexName() != null && translatedQuery.getIndexName().equals( TEST_WORKSPACE.getSearchIndexName() )
        translatedQuery.getIndexType() != null && translatedQuery.getIndexType().equals( IndexType.NODE );
        translatedQuery.getSortBuilders() != null;
        translatedQuery.getSortBuilders().size() == 1;
        translatedQuery.getSortBuilders().iterator().next() instanceof GeoDistanceSortBuilder
    }
}
