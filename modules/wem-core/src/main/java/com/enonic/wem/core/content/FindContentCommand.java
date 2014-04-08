package com.enonic.wem.core.content;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.content.query.ContentQueryResult;
import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.core.index.entity.EntityQueryResult;
import com.enonic.wem.core.index.entity.EntityQueryResultEntry;
import com.enonic.wem.core.index.entity.EntityQueryService;

final class FindContentCommand
{
    private ContentQueryEntityQueryTranslator translator = new ContentQueryEntityQueryTranslator();

    private EntityQueryService entityQueryService;

    private ContentQuery contentQuery;

    ContentQueryResult execute()
    {
        final EntityQuery entityQuery = translator.translate( this.contentQuery );
        final EntityQueryResult entityQueryResult = entityQueryService.find( entityQuery );

        return translateToContentIndexQueryResult( entityQueryResult );
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

    FindContentCommand entityQueryService( final EntityQueryService entityQueryService )
    {
        this.entityQueryService = entityQueryService;
        return this;
    }

    FindContentCommand contentQuery( final ContentQuery contentQuery )
    {
        this.contentQuery = contentQuery;
        return this;
    }
}
