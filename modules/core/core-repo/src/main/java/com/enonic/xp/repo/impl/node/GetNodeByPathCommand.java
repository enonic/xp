package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;

public class GetNodeByPathCommand
    extends AbstractNodeCommand
{
    private final NodePath path;

    private GetNodeByPathCommand( final Builder builder )
    {
        super( builder );
        path = builder.path;
    }

    public Node execute()
    {
        return this.nodeStorageService.get( path, InternalContext.from( ContextAccessor.current() ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath path;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder nodePath( NodePath path )
        {
            this.path = path;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( this.path, "path is required" );
        }

        public GetNodeByPathCommand build()
        {
            this.validate();
            return new GetNodeByPathCommand( this );
        }
    }
}
