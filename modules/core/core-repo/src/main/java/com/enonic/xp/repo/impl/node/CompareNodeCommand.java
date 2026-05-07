package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;

import static java.util.Objects.requireNonNull;

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

        final NodeBranchEntry sourceWsVersion = nodeStorageService.getNodeBranchEntry( this.nodeId, InternalContext.from( context ) );
        final NodeBranchEntry targetWsVersion =
            nodeStorageService.getNodeBranchEntry( this.nodeId, InternalContext.create( context ).branch( this.target ).build() );

        return CompareStatusResolver.create()
            .source( sourceWsVersion )
            .target( targetWsVersion )
            .storageService( this.nodeStorageService )
            .build()
            .resolve();
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

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( nodeId, "nodeId is required" );
        }

        public CompareNodeCommand build()
        {
            this.validate();
            return new CompareNodeCommand( this );
        }
    }
}
