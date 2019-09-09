package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
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
        final Context context = ContextAccessor.current();

        return this.nodeStorageService.getNode( nodeId, versionId, InternalContext.from( context ) );
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
            Preconditions.checkNotNull( this.nodeId );
            Preconditions.checkNotNull( this.versionId );
        }

        public GetNodeByIdAndVersionIdCommand build()
        {
            this.validate();
            return new GetNodeByIdAndVersionIdCommand( this );
        }

    }

}
