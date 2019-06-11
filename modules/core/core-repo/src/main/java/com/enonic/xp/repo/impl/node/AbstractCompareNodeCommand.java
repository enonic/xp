package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.InternalContext;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

class AbstractCompareNodeCommand
{
    final Branch target;

    final NodeStorageService nodeStorageService;

    AbstractCompareNodeCommand( Builder builder )
    {
        target = builder.target;
        this.nodeStorageService = builder.nodeStorageService;
    }

    NodeComparison doCompareNodeVersions( final Context context, final NodeId nodeId )
    {
        final NodeBranchEntry sourceWsVersion = nodeStorageService.getBranchNodeVersion( nodeId, InternalContext.from( context ) );
        final NodeBranchEntry targetWsVersion = nodeStorageService.getBranchNodeVersion( nodeId, InternalContext.create( context ).
            branch( this.target ).
            build() );

        final CompareStatus compareStatus = CompareStatusResolver.create().
            source( sourceWsVersion ).
            target( targetWsVersion ).
            storageService( this.nodeStorageService ).
            build().
            resolve();

        return new NodeComparison( sourceWsVersion, targetWsVersion, compareStatus );
    }


    public static class Builder<B extends Builder>
    {
        private Branch target;

        private NodeStorageService nodeStorageService;

        @SuppressWarnings("unchecked")
        public B target( final Branch target )
        {
            this.target = target;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B storageService( final NodeStorageService nodeStorageService )
        {
            this.nodeStorageService = nodeStorageService;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( nodeStorageService );
        }
    }
}
