package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repo.impl.InternalContext;

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

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }


    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePaths paths;

        private Builder()
        {
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
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
            Objects.requireNonNull( this.paths, "paths is required" );
        }

        public GetNodesByPathsCommand build()
        {
            this.validate();
            return new GetNodesByPathsCommand( this );
        }
    }
}
