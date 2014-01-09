package com.enonic.wem.core.content;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import com.enonic.wem.api.command.content.FindContent;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.content.query.ContentQueryResult;
import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.index.entity.EntityQueryResult;
import com.enonic.wem.core.index.entity.EntitySearchResultEntry;
import com.enonic.wem.core.index.entity.EntitySearchService;

public class FindContentHandler
    extends CommandHandler<FindContent>
{
    private ContentQueryEntityQueryTranslator translator = new ContentQueryEntityQueryTranslator();

    private EntitySearchService entitySearchService;

    @Override
    public void handle()
        throws Exception
    {
        final ContentQuery contentQuery = command.getContentQuery();

        final EntityQuery entityQuery = translator.translate( contentQuery );

        final EntityQueryResult entityQueryResult = entitySearchService.find( entityQuery );

        ContentQueryResult contentIndexQueryResult = translateToContentIndexQueryResult( entityQueryResult );

        command.setResult( contentIndexQueryResult );
    }


    private ContentQueryResult translateToContentIndexQueryResult( final EntityQueryResult result )
    {
        final ContentQueryResult contentQueryResult = new ContentQueryResult( new Long( result.getTotalHits() ).intValue() );

        final ImmutableSet<EntitySearchResultEntry> entries = result.getEntries();

        for ( final EntitySearchResultEntry entry : entries )
        {
            contentQueryResult.addContentHit( ContentId.from( entry.getId() ), entry.getScore() );
        }
        return contentQueryResult;
    }


    @Inject
    public void setEntitySearchService( final EntitySearchService entitySearchService )
    {
        this.entitySearchService = entitySearchService;
    }
}
