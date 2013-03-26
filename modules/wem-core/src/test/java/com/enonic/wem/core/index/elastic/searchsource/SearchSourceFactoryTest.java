package com.enonic.wem.core.index.elastic.searchsource;

import org.codehaus.jackson.JsonNode;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.core.index.elastic.AbstractJsonTest;

public class SearchSourceFactoryTest
    extends AbstractJsonTest
{

    @Test
    public void testFullText()
        throws Exception
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( "test" );

        final SearchSourceBuilder searchSourceBuilder = SearchSourceFactory.create( contentIndexQuery );

        assertJson( "fulltext_result.json", searchSourceBuilder );
    }

    @Test
    public void testContentTypeFilter()
        throws Exception
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( "test" );
        contentIndexQuery.setContentTypeNames(
            QualifiedContentTypeNames.from( "contentTypes:myContentType1", "contentTypes:myContentType2" ) );

        final SearchSourceBuilder searchSourceBuilder = SearchSourceFactory.create( contentIndexQuery );

        assertJson( "contentTypeFilter_result.json", searchSourceBuilder );
    }

    private void assertJson( final String fileName, final SearchSourceBuilder searchSourceBuilder )
        throws Exception
    {
        final JsonNode expected = parseJson( fileName );
        final JsonNode actual = parseJsonString( searchSourceBuilder.toString() );

        assertJson( expected, actual );
    }


}
