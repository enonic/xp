package com.enonic.wem.core.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.FindContent;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.index.content.ContentSearchHit;
import com.enonic.wem.core.index.content.ContentSearchResults;
import com.enonic.wem.core.index.search.SearchService;

import static junit.framework.Assert.assertEquals;

public class FindContentHandlerTest
    extends AbstractCommandHandlerTest
{

    private FindContentHandler handler;

    private SearchService searchService;

    @Before
    public void setUp()
        throws Exception
    {

        searchService = Mockito.mock( SearchService.class );

        handler = new FindContentHandler();

        handler.setSearchService( searchService );
    }

    @Test
    public void testFindContent()
        throws Exception
    {

        ContentSearchResults contentSearchResults = new ContentSearchResults( 5, 0 );
        contentSearchResults.add( new ContentSearchHit( ContentId.from( "1" ), 1.0f ) );

        Mockito.when( searchService.search( Mockito.isA( ContentIndexQuery.class ) ) ).thenReturn( contentSearchResults );

        FindContentHandler findContentHandler = new FindContentHandler();
        findContentHandler.setSearchService( searchService );

        ContentIndexQuery contentIndexQuery = new ContentIndexQuery();
        contentIndexQuery.setFullTextSearchString( "testing" );

        final FindContent findContent = Commands.content().find().query( contentIndexQuery );

        findContentHandler.handle( this.context, findContent );

        final ContentIndexQueryResult result = findContent.getResult();

        assertEquals( 5, result.getTotalSize() );
        assertEquals( 1, result.getContentQueryHits().size() );

    }
}
