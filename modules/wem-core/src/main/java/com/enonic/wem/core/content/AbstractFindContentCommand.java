package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.query.ContentQueryResult;
import com.enonic.wem.core.index.query.NodeQueryResult;
import com.enonic.wem.core.index.query.NodeQueryResultEntry;
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

    protected ContentQueryResult translateToContentQueryResult( final NodeQueryResult result )
    {
        final ContentQueryResult.Builder builder = ContentQueryResult.newResult( result.getTotalHits() );
        final ImmutableSet<NodeQueryResultEntry> entries = result.getEntries();

        for ( final NodeQueryResultEntry entry : entries )
        {
            builder.addContentHit( ContentId.from( entry.getId() ), entry.getScore() );
        }

        builder.setAggregations( result.getAggregations() );

        return builder.build();
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



