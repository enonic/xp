package com.enonic.wem.core.index.query

import com.enonic.wem.api.data.Value
import com.enonic.wem.core.index.Index
import com.enonic.wem.core.index.IndexType
import com.enonic.wem.query.EntityQuery
import com.enonic.wem.query.parser.QueryParser
import com.enonic.wem.query.queryfilter.QueryFilter
import org.elasticsearch.index.query.MatchAllQueryBuilder
import org.elasticsearch.index.query.RangeQueryBuilder
import org.elasticsearch.index.query.TermsFilterBuilder
import spock.lang.Specification

class EntityQueryTranslatorTest extends Specification
{
    def "query values populated"( )
    {
        given:
        EntityQueryTranslator entityQueryTranslator = new EntityQueryTranslator();
        EntityQuery entityQuery = EntityQuery.newQuery().query( QueryParser.parse( "myField >= 1" ) ).build();

        when:
        def translatedQuery = entityQueryTranslator.translate( entityQuery )

        then:
        translatedQuery.getQuery() != null;
        translatedQuery.getQuery() instanceof RangeQueryBuilder
        translatedQuery.getIndex() != null && translatedQuery.getIndex().equals( Index.NODB );
        translatedQuery.getIndexType() != null && translatedQuery.getIndexType().equals( IndexType.ENTITY );
        translatedQuery.getFilter() == null;
        translatedQuery.getFacet() == null;
    }

    def "filter values populated"( )
    {
        given:
        EntityQueryTranslator entityQueryTranslator = new EntityQueryTranslator();

        def queryFilter = QueryFilter.newValueQueryFilter().
                fieldName( "myField" ).
                add( new Value.String( "myValue" ) ).
                add( new Value.String( "mySecondValue" ) ).
                build()

        EntityQuery entityQuery = EntityQuery.newQuery().addFilter( queryFilter ).build();

        when:
        def translatedQuery = entityQueryTranslator.translate( entityQuery )

        then:
        translatedQuery.getQuery() instanceof MatchAllQueryBuilder;
        translatedQuery.getIndex() != null && translatedQuery.getIndex().equals( Index.NODB );
        translatedQuery.getIndexType() != null && translatedQuery.getIndexType().equals( IndexType.ENTITY );
        translatedQuery.getFilter() != null;
        translatedQuery.getFilter() instanceof TermsFilterBuilder
        translatedQuery.getFacet() == null;
    }

}
