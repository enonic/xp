package com.enonic.wem.core.content

import com.enonic.wem.api.content.query.ContentQuery
import com.enonic.wem.api.entity.query.EntityQuery
import com.enonic.wem.api.query.filter.Filter
import com.enonic.wem.api.query.filter.ValueFilter
import com.enonic.wem.api.query.parser.QueryParser
import com.enonic.wem.api.schema.content.ContentTypeNames
import spock.lang.Specification

class ContentQueryEntityQueryTranslatorTest
    extends Specification
{
    def "plain"()
    {
        given:
        ContentQueryEntityQueryTranslator translator = new ContentQueryEntityQueryTranslator();

        when:
        ContentQuery contentQuery = ContentQuery.newContentQuery().
            queryExpr( QueryParser.parse( "status = 2" ) ).
            build();

        EntityQuery entityQuery = translator.translate( contentQuery );

        then:
        entityQuery.queryFilters.getSize() == 1;

    }


    def "with content name filter"()
    {
        given:
        ContentQueryEntityQueryTranslator translator = new ContentQueryEntityQueryTranslator();

        when:
        ContentQuery contentQuery = ContentQuery.newContentQuery().
            queryExpr( QueryParser.parse( "status = 2" ) ).
            addContentTypeNames( ContentTypeNames.from( "contenttype1", "contenttype2" ) ).
            build();

        EntityQuery entityQuery = translator.translate( contentQuery );

        then:
        entityQuery.queryFilters != null
        entityQuery.queryFilters.getSize() == 2;

        for ( Filter filter : entityQuery.queryFilters )
        {
            filter instanceof ValueFilter
        }
    }

}
