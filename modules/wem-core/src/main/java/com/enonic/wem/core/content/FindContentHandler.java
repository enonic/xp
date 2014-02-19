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
import com.enonic.wem.core.index.entity.EntityQueryResultEntry;
import com.enonic.wem.core.index.entity.EntityQueryService;

public class FindContentHandler
    extends CommandHandler<FindContent>
{
    private ContentQueryEntityQueryTranslator translator = new ContentQueryEntityQueryTranslator();

    private EntityQueryService entityQueryService;

    @Override
    public void handle()
        throws Exception
    {
        final ContentQuery contentQuery = command.getContentQuery();

        final EntityQuery entityQuery = translator.translate( contentQuery );

        final EntityQueryResult entityQueryResult = entityQueryService.find( entityQuery );

        ContentQueryResult contentIndexQueryResult = translateToContentIndexQueryResult( entityQueryResult );

        command.setResult( contentIndexQueryResult );
    }

    private ContentQueryResult translateToContentIndexQueryResult( final EntityQueryResult result )
    {
        final ContentQueryResult.Builder builder = ContentQueryResult.newResult( result.getTotalHits() );

        final ImmutableSet<EntityQueryResultEntry> entries = result.getEntries();

        for ( final EntityQueryResultEntry entry : entries )
        {
            builder.addContentHit( ContentId.from( entry.getId() ), entry.getScore() );
        }

        builder.setAggregations( result.getAggregations() );

        return builder.build();
    }

    @Inject
    public void setEntityQueryService( final EntityQueryService entityQueryService )
    {
        this.entityQueryService = entityQueryService;
    }
}
