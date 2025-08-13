package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentPathsByQueryResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeQuery;

final class FindContentPathsByQueryCommand
    extends AbstractContentCommand
{
    private final ContentQuery contentQuery;

    private FindContentPathsByQueryCommand( final Builder builder )
    {
        super( builder );
        this.contentQuery = builder.contentQuery;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public FindContentPathsByQueryResult execute()
    {
        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( this.contentQuery ).
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
        private ContentQuery contentQuery;

        private Builder()
        {
        }

        public Builder contentQuery( final ContentQuery contentQuery )
        {
            this.contentQuery = contentQuery;
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
            Preconditions.checkNotNull( contentQuery );
        }

    }
}
