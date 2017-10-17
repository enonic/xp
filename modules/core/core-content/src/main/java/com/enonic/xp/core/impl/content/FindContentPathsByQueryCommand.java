package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.content.FindContentPathsByQueryParams;
import com.enonic.xp.node.FindNodePathsByQueryResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;

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

    public ContentPaths execute()
    {
        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( this.params.getContentQuery() ).
            addQueryFilters( createFilters() ).
            build();

        final FindNodePathsByQueryResult result = nodeService.findNodePathsByQuery( nodeQuery );

        return ContentNodeHelper.translateNodePathsToContentPaths( result.getPaths() );
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
