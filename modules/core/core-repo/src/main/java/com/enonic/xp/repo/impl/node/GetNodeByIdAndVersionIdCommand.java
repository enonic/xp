package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.InternalContext;

public class GetNodeByIdAndVersionIdCommand
    extends AbstractNodeCommand
{

    private final NodeId nodeId;

    private final NodeVersionId versionId;

    private GetNodeByIdAndVersionIdCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
        this.versionId = builder.versionId;
    }

    public Node execute()
    {
        final Node node = this.nodeStorageService.get( versionId, InternalContext.from( ContextAccessor.current() ) );

        if ( node == null || !node.id().equals( nodeId ) )
        {
            return null;
        }

        return node;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {

        private NodeId nodeId;

        private NodeVersionId versionId;

        private Builder()
        {
            super();
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder versionId( NodeVersionId versionId )
        {
            this.versionId = versionId;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( this.nodeId, "nodeId is required" );
            Objects.requireNonNull( this.versionId, "versionId is required" );
        }

        public GetNodeByIdAndVersionIdCommand build()
        {
            this.validate();
            return new GetNodeByIdAndVersionIdCommand( this );
        }

    }

}
