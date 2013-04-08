package com.enonic.wem.core.index.elastic.searchsource;

import org.codehaus.jackson.JsonNode;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.space.SpaceNames;
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

    @Test
    public void testSpacesFilter()
        throws Exception
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( "test" );
        contentIndexQuery.setSpaceNames( SpaceNames.from( "mySpace1", "mySpace2" ) );

        final SearchSourceBuilder searchSourceBuilder = SearchSourceFactory.create( contentIndexQuery );

        assertJson( "spacesFilter_result.json", searchSourceBuilder );
    }

    @Test
    public void testSingleLowerRangeFilter()
        throws Exception
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( "test" );
        contentIndexQuery.addRange( new DateTime( 2013, 1, 1, 1, 1, DateTimeZone.UTC ), null );

        final SearchSourceBuilder searchSourceBuilder = SearchSourceFactory.create( contentIndexQuery );

        assertJson( "singleLowerRangeFilter_result.json", searchSourceBuilder );
    }

    @Test
    public void testMultipleLowerRangeFilters()
        throws Exception
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( "test" );
        contentIndexQuery.addRange( new DateTime( 2013, 1, 1, 1, 1, DateTimeZone.UTC ), null );
        contentIndexQuery.addRange( new DateTime( 2014, 1, 1, 1, 1, DateTimeZone.UTC ), null );

        final SearchSourceBuilder searchSourceBuilder = SearchSourceFactory.create( contentIndexQuery );

        assertJson( "multipleLowerRangeFilters_result.json", searchSourceBuilder );
    }


    @Test
    public void testSingleUpperRangeFilter()
        throws Exception
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( "test" );
        contentIndexQuery.addRange( null, new DateTime( 2013, 1, 1, 1, 1, DateTimeZone.UTC ) );

        final SearchSourceBuilder searchSourceBuilder = SearchSourceFactory.create( contentIndexQuery );

        assertJson( "singleUpperRangeFilter_result.json", searchSourceBuilder );


    }


    // Warning: Because of Elasticsearch generating a strange JSON, this test will actually nerf all but the last
    // filter when doing the compare both for source file and searchSourceBuilder, making it difficult to test
    // Hoping for ES fix of this issue
    @Ignore
    @Test
    public void testMultipleFilters()
        throws Exception
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( "test" );
        contentIndexQuery.setSpaceNames( SpaceNames.from( "mySpace1", "mySpace2" ) );
        contentIndexQuery.setContentTypeNames(
            QualifiedContentTypeNames.from( "contentTypes:myContentType1", "contentTypes:myContentType2" ) );

        final SearchSourceBuilder searchSourceBuilder = SearchSourceFactory.create( contentIndexQuery );

        assertJson( "multipleFilters_result.json", searchSourceBuilder );
    }


    private void assertJson( final String fileName, final SearchSourceBuilder searchSourceBuilder )
        throws Exception
    {
        final JsonNode expected = parseJson( fileName );
        final JsonNode actual = parseJsonString( searchSourceBuilder.toString() );

        assertJson( expected, actual );
    }


}
