package com.enonic.wem.core.content;

import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.query.ContentQueryResult;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.core.index.query.QueryResult;
import com.enonic.wem.core.index.query.QueryResultEntry;
import com.enonic.wem.core.index.query.QueryService;

public abstract class AbstractFindContentCommand
    extends AbstractContentCommand
{
    protected final QueryService queryService;

    public AbstractFindContentCommand( final Builder builder )
    {
        super( builder );
        this.queryService = builder.queryService;
    }

    protected ContentQueryResult translateToContentQueryResult( final QueryResult result )
    {
        final ContentQueryResult.Builder builder = ContentQueryResult.newResult( result.getTotalHits() );
        final ImmutableSet<QueryResultEntry> entries = result.getEntries();

        for ( final QueryResultEntry entry : entries )
        {
            builder.addContentHit( ContentId.from( entry.getId() ), entry.getScore() );
        }

        builder.setAggregations( result.getAggregations() );

        return builder.build();
    }

    protected EntityIds getAsEntityIds( final ContentIds contentIds )
    {
        final Set<EntityId> entityIds = Sets.newHashSet();

        final Iterator<ContentId> iterator = contentIds.iterator();

        while ( iterator.hasNext() )
        {
            entityIds.add( EntityId.from( iterator.next().toString() ) );
        }

        return EntityIds.from( entityIds );
    }


    public static class Builder<B extends Builder>
        extends AbstractContentCommand.Builder<B>
    {
        private QueryService queryService;

        @SuppressWarnings("unchecked")
        public B queryService( final QueryService queryService )
        {
            this.queryService = queryService;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( queryService );
        }

    }

}



