package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.Contents;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.Nodes;

import static java.util.Objects.requireNonNull;


final class GetContentByPathsCommand
    extends AbstractContentCommand
{
    private final ContentPaths contentPaths;

    private final boolean allowRoot;

    private GetContentByPathsCommand( final Builder builder )
    {
        super( builder );
        this.contentPaths = builder.contentPaths;
        this.allowRoot = builder.allowRoot;
    }

    Contents execute()
    {
        final Contents contents = doExecute();
        return filter( contents );
    }

    private Contents doExecute()
    {
        final NodePaths paths = ContentNodeHelper.translateContentPathsToNodePaths( contentPaths );
        final Nodes nodes = nodeService.getByPaths( paths );

        final Nodes filteredNodes =
            allowRoot ? nodes : nodes.stream().filter( n -> !isProtectedRoot( n.path() ) ).collect( Nodes.collector() );

        return ContentNodeTranslator.fromNodes( filteredNodes );
    }

    public static Builder create( final ContentPaths contentPaths )
    {
        return new Builder( contentPaths );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final ContentPaths contentPaths;

        private boolean allowRoot;

        Builder( final ContentPaths contentPaths )
        {
            this.contentPaths = contentPaths;
        }

        public Builder allowRoot()
        {
            this.allowRoot = true;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( contentPaths, "contentPaths is required" );
        }

        public GetContentByPathsCommand build()
        {
            validate();
            return new GetContentByPathsCommand( this );
        }
    }
}
