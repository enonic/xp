package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.Contents;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.Nodes;


final class GetContentByPathsCommand
    extends AbstractContentCommand
{
    private final ContentPaths contentPaths;

    private GetContentByPathsCommand( final Builder builder )
    {
        super( builder );
        this.contentPaths = builder.contentPaths;
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

        return this.translator.fromNodes( nodes, true );
    }

    public static Builder create( final ContentPaths contentPaths )
    {
        return new Builder( contentPaths );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final ContentPaths contentPaths;

        Builder( final ContentPaths contentPaths )
        {
            this.contentPaths = contentPaths;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( contentPaths, "contentPaths is required" );
        }

        public GetContentByPathsCommand build()
        {
            validate();
            return new GetContentByPathsCommand( this );
        }
    }
}
