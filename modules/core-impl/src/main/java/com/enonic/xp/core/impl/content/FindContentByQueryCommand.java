package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.content.Contents;
import com.enonic.xp.core.content.FindContentByQueryParams;
import com.enonic.xp.core.content.FindContentByQueryResult;
import com.enonic.xp.core.node.FindNodesByQueryResult;
import com.enonic.xp.core.node.NodeQuery;

final class FindContentByQueryCommand
    extends AbstractContentCommand
{
    private final FindContentByQueryParams params;

    private FindContentByQueryCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    FindContentByQueryResult execute()
    {
        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( this.params.getContentQuery() );

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        Contents contents = this.translator.fromNodes( result.getNodes() );

        return FindContentByQueryResult.create().
            contents( contents ).
            aggregations( result.getAggregations() ).
            hits( result.getHits() ).
            totalHits( result.getTotalHits() ).
            build();
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private FindContentByQueryParams params;

        private Builder()
        {
        }

        public Builder params( final FindContentByQueryParams params )
        {
            this.params = params;
            return this;
        }

        public FindContentByQueryCommand build()
        {
            validate();
            return new FindContentByQueryCommand( this );
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
            Preconditions.checkNotNull( params.getContentQuery() );
        }

    }
}
