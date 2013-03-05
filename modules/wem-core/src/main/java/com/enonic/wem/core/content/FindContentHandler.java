package com.enonic.wem.core.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.FindContent;
import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.api.content.query.ContentQueryHits;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.index.content.ContentSearchHit;
import com.enonic.wem.core.index.content.ContentSearchResults;
import com.enonic.wem.core.index.search.SearchService;

@Component
public class FindContentHandler
    extends CommandHandler<FindContent>
{

    private SearchService searchService;

    public FindContentHandler()
    {
        super( FindContent.class );
    }

    @Override
    public void handle( final CommandContext context, final FindContent command )
        throws Exception
    {

        final ContentIndexQuery contentIndexQuery = command.getContentIndexQuery();

        final ContentSearchResults searchResults = searchService.search( contentIndexQuery );

        ContentQueryHits contentQueryHits = new ContentQueryHits( searchResults.getTotal() );

        for ( ContentSearchHit hit : searchResults.getHits() )
        {
            contentQueryHits.addContentHit( hit.getContentId(), hit.getScore() );
        }

        command.setResult( contentQueryHits );
    }

    @Autowired
    public void setSearchService( final SearchService searchService )
    {
        this.searchService = searchService;
    }
}
