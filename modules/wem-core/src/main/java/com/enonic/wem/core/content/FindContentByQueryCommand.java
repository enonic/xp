package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByQueryParams;
import com.enonic.wem.api.content.FindContentByQueryResult;
import com.enonic.wem.repo.FindNodesByQueryResult;
import com.enonic.wem.repo.NodeQuery;

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
