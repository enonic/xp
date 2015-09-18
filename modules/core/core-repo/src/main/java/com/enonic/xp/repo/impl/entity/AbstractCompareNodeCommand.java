package com.enonic.xp.repo.impl.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.Context;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.branch.BranchContext;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.elasticsearch.branch.NodeBranchVersion;
import com.enonic.xp.repo.impl.version.VersionService;

public class AbstractCompareNodeCommand
{
    private final Branch target;

    private final VersionService versionService;

    private final BranchService branchService;

    AbstractCompareNodeCommand( Builder builder )
    {
        target = builder.target;
        versionService = builder.versionService;
        this.branchService = builder.branchService;
    }

    NodeComparison doCompareNodeVersions( final Context context, final NodeId nodeId )
    {
        final NodeBranchVersion sourceWsVersion = this.branchService.get( nodeId, BranchContext.from( context ) );
        final NodeBranchVersion targetWsVersion =
            this.branchService.get( nodeId, BranchContext.from( this.target, context.getRepositoryId() ) );

        final CompareStatus compareStatus = CompareStatusResolver.create().
            repositoryId( context.getRepositoryId() ).
            source( sourceWsVersion ).
            target( targetWsVersion ).
            versionService( this.versionService ).
            build().
            resolve();

        return new NodeComparison( nodeId, compareStatus );
    }


    public static class Builder<B extends Builder>
    {
        private Branch target;

        private VersionService versionService;

        private BranchService branchService;

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
        public B versionService( final VersionService versionService )
        {
            this.versionService = versionService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B branchService( final BranchService branchService )
        {
            this.branchService = branchService;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( versionService );
        }

        public AbstractCompareNodeCommand build()
        {
            return new AbstractCompareNodeCommand( this );
        }
    }
}
