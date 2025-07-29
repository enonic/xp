package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.FindContentPathsByQueryParams;
import com.enonic.xp.content.FindContentPathsByQueryResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeQuery;

final class FindContentPathsByQueryCommand
    extends AbstractContentCommand
{
    private final FindContentPathsByQueryParams params;

    private FindContentPathsByQueryCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public FindContentPathsByQueryResult execute()
    {
        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( this.params.getContentQuery() ).
            addQueryFilters( createFilters() ).
            withPath( true ).
            build();

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        return FindContentPathsByQueryResult.create()
            .contentPaths( result.getNodeHits()
                                                  .stream()
                                                  .map( NodeHit::getNodePath )
                                                  .map( ContentNodeHelper::translateNodePathToContentPath )
                                                  .collect( ContentPaths.collector() ) )
            .hits( result.getHits() ).
            totalHits( result.getTotalHits() ).
            build();
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private FindContentPathsByQueryParams params;

        private Builder()
        {
        }

        public Builder params( final FindContentPathsByQueryParams params )
        {
            this.params = params;
            return this;
        }

        public FindContentPathsByQueryCommand build()
        {
            validate();
            return new FindContentPathsByQueryCommand( this );
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
