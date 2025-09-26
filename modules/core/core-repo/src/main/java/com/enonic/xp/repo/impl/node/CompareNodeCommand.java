package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;

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

        final NodeBranchEntry sourceWsVersion = nodeStorageService.getBranchNodeVersion( this.nodeId, InternalContext.from( context ) );
        final NodeBranchEntry targetWsVersion = nodeStorageService.getBranchNodeVersion( this.nodeId, InternalContext.create( context ).
            branch( this.target ).
            build() );

        return CompareStatusResolver.create().
            source( sourceWsVersion ).
            target( targetWsVersion ).
            storageService( this.nodeStorageService ).
            build().
            resolve();
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
            Objects.requireNonNull( nodeId, "nodeId is required" );
        }

        public CompareNodeCommand build()
        {
            this.validate();
            return new CompareNodeCommand( this );
        }
    }
}
