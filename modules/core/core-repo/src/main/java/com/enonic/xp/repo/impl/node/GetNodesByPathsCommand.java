package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repo.impl.InternalContext;

import static java.util.Objects.requireNonNull;

public class GetNodesByPathsCommand
    extends AbstractNodeCommand
{
    private final NodePaths nodePaths;

    private GetNodesByPathsCommand( Builder builder )
    {
        super( builder );
        this.nodePaths = builder.paths;
    }

    public Nodes execute()
    {
        return this.nodeStorageService.get( nodePaths, InternalContext.from( ContextAccessor.current() ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePaths paths;

        private Builder()
        {
        }

        public Builder paths( NodePaths paths )
        {
            this.paths = paths;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( this.paths, "paths is required" );
        }

        public GetNodesByPathsCommand build()
        {
            this.validate();
            return new GetNodesByPathsCommand( this );
        }
    }
}
