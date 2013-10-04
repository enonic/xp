package com.enonic.wem.core.content;


import com.google.inject.Inject;

import com.enonic.wem.api.command.content.FindContent;
import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.index.content.ContentSearchHit;
import com.enonic.wem.core.index.content.ContentSearchResults;
import com.enonic.wem.core.index.search.SearchService;


public class FindContentHandler
    extends CommandHandler<FindContent>
{
    private SearchService searchService;

    @Override
    public void handle( final CommandContext context, final FindContent command )
        throws Exception
    {
        final ContentIndexQuery contentIndexQuery = command.getContentIndexQuery();

        final ContentSearchResults searchResults = searchService.search( contentIndexQuery );

        ContentIndexQueryResult contentIndexQueryResult = new ContentIndexQueryResult( searchResults.getTotal() );

        contentIndexQueryResult.setFacets( searchResults.getFacets() );

        for ( ContentSearchHit hit : searchResults.getHits() )
        {
            contentIndexQueryResult.addContentHit( hit.getContentId(), hit.getScore() );
        }

        command.setResult( contentIndexQueryResult );
    }

    @Inject
    public void setSearchService( final SearchService searchService )
    {
        this.searchService = searchService;
    }
}
