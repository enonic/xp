package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;

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

        final Nodes foundNodes = result.getNodes();

        if ( this.params.isPopulateChildren() )
        {
            final Nodes.Builder withChildren = Nodes.create();


        }

        Contents contents = this.oldTranslator.fromNodes( foundNodes );

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

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
            Preconditions.checkNotNull( params.getContentQuery() );
        }

    }
}
