package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.storage.StorageService;
import com.enonic.wem.repo.internal.storage.branch.BranchNodeVersion;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.Context;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;

public class AbstractCompareNodeCommand
{
    private final Branch target;

    private final StorageService storageService;

    AbstractCompareNodeCommand( Builder builder )
    {
        target = builder.target;
        this.storageService = builder.storageService;
    }

    NodeComparison doCompareNodeVersions( final Context context, final NodeId nodeId )
    {
        final BranchNodeVersion sourceWsVersion = storageService.getBranchNodeVersion( nodeId, InternalContext.from( context ) );
        final BranchNodeVersion targetWsVersion = storageService.getBranchNodeVersion( nodeId, InternalContext.create( context ).
            branch( this.target ).
            build() );

        final CompareStatus compareStatus = CompareStatusResolver.create().
            source( sourceWsVersion ).
            target( targetWsVersion ).
            storageService( this.storageService ).
            build().
            resolve();

        return new NodeComparison( nodeId, compareStatus );
    }


    public static class Builder<B extends Builder>
    {
        private Branch target;

        private StorageService storageService;

        Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public B target( final Branch target )
        {
            this.target = target;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B storageService( final StorageService storageService )
        {
            this.storageService = storageService;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( storageService );
        }

        public AbstractCompareNodeCommand build()
        {
            return new AbstractCompareNodeCommand( this );
        }
    }
}
