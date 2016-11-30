package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;

final class FindContentIdsByQueryCommand
    extends AbstractContentCommand
{
    private final ContentQuery query;

    private FindContentIdsByQueryCommand( final Builder builder )
    {
        super( builder );
        this.query = builder.query;
    }

    public static Builder create()
    {
        return new Builder();
    }

    FindContentIdsByQueryResult execute()
    {
        final NodeQuery.Builder nodeQuery = ContentQueryNodeQueryTranslator.translate( this.query );
        addFilters( nodeQuery );

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery.build() );

        return FindContentIdsByQueryResult.create().
            contents( ContentNodeHelper.toContentIds( result.getNodeIds() ) ).
            aggregations( result.getAggregations() ).
            hits( result.getHits() ).
            totalHits( result.getTotalHits() ).
            build();
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentQuery query;

        private Builder()
        {
        }

        public Builder query( final ContentQuery query )
        {
            this.query = query;
            return this;
        }

        public FindContentIdsByQueryCommand build()
        {
            validate();
            return new FindContentIdsByQueryCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( query );
        }

    }
}
