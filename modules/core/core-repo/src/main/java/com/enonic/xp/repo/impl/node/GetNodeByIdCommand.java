package com.enonic.xp.repo.impl.node;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;

import static java.util.Objects.requireNonNull;

public class GetNodeByIdCommand
    extends AbstractNodeCommand
{
    private final NodeId id;

    private GetNodeByIdCommand( final Builder builder )
    {
        super( builder );
        id = builder.id;
    }

    public Node execute()
    {
        return doGetById( id );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        private Builder()
        {
            super();
        }

        public Builder id( NodeId id )
        {
            this.id = id;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( this.id, "id is required" );
        }

        public GetNodeByIdCommand build()
        {
            this.validate();
            return new GetNodeByIdCommand( this );
        }
    }
}
