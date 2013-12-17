package com.enonic.wem.core.content;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import com.enonic.wem.api.command.content.FindContent;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.query.EntityQuery;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.index.entity.EntityQueryResult;
import com.enonic.wem.core.index.entity.EntitySearchResultEntry;
import com.enonic.wem.core.index.entity.EntitySearchService;
import com.enonic.wem.core.index.search.SearchService;

public class FindContentHandler
    extends CommandHandler<FindContent>
{
    private SearchService searchService;

    private EntitySearchService entitySearchService;

    @Override
    public void handle()
        throws Exception
    {
        final EntityQuery query = EntityQuery.newQuery().addFilter(
            Filter.newValueQueryFilter().fieldName( "_collection" ).add( new Value.String( "content" ) ).build() ).build();

        final EntityQueryResult result = entitySearchService.find( query );

        ContentIndexQueryResult contentIndexQueryResult = translateToContentIndexQueryResult( result );

        command.setResult( contentIndexQueryResult );
    }

    private ContentIndexQueryResult translateToContentIndexQueryResult( final EntityQueryResult result )
    {
        final ContentIndexQueryResult contentIndexQueryResult = new ContentIndexQueryResult( new Long( result.getTotalHits() ).intValue() );

        final ImmutableSet<EntitySearchResultEntry> entries = result.getEntries();

        for ( final EntitySearchResultEntry entry : entries )
        {
            contentIndexQueryResult.addContentHit( ContentId.from( entry.getId() ), entry.getScore() );
        }
        return contentIndexQueryResult;
    }


    @Inject
    public void setSearchService( final SearchService searchService )
    {
        this.searchService = searchService;
    }

    @Inject
    public void setEntitySearchService( final EntitySearchService entitySearchService )
    {
        this.entitySearchService = entitySearchService;
    }
}
