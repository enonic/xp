package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.context.Context;
import com.enonic.xp.core.context.ContextAccessor;
import com.enonic.xp.core.node.NodeComparison;
import com.enonic.xp.core.node.NodeId;

public class CompareNodeCommand
    extends AbstractCompareNodeCommand
{
    private final NodeId nodeId;


    private CompareNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeComparison execute()
    {
        final Context context = ContextAccessor.current();

        return doCompareNodeVersions( context, this.nodeId );
    }

    public static final class Builder
        extends AbstractCompareNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeId );
        }

        public CompareNodeCommand build()
        {
            this.validate();
            return new CompareNodeCommand( this );
        }
    }
}