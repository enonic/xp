package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByQueryParams;
import com.enonic.wem.api.content.FindContentByQueryResult;
import com.enonic.wem.api.entity.FindNodesByQueryResult;
import com.enonic.wem.api.entity.query.NodeQuery;

final class FindContentByQueryCommand
    extends AbstractFindContentCommand
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

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery, context );

        Contents contents = this.translator.fromNodes( result.getNodes() );

        if ( !contents.isEmpty() && this.params.isPopulateChildren() )
        {
            contents = ChildContentIdsResolver.create().
                queryService( this.queryService ).
                translator( this.translator ).
                blobService( this.blobService ).
                contentTypeService( this.contentTypeService ).
                context( this.context ).
                nodeService( this.nodeService ).
                build().
                resolve( contents );
        }

        return FindContentByQueryResult.create().
            contents( contents ).
            aggregations( result.getAggregations() ).
            hits( result.getHits() ).
            totalHits( result.getTotalHits() ).
            build();
    }

    public static final class Builder
        extends AbstractFindContentCommand.Builder<Builder>
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
