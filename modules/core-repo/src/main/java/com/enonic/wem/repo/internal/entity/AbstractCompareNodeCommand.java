package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.node.NodeComparison;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.internal.index.query.NodeWorkspaceVersion;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.wem.repo.internal.workspace.WorkspaceContext;
import com.enonic.wem.repo.internal.workspace.WorkspaceService;

public class AbstractCompareNodeCommand
{
    private final Workspace target;

    private final VersionService versionService;

    private final WorkspaceService workspaceService;

    AbstractCompareNodeCommand( Builder builder )
    {
        target = builder.target;
        versionService = builder.versionService;
        this.workspaceService = builder.workspaceService;
    }

    NodeComparison doCompareNodeVersions( final Context context, final NodeId nodeId )
    {
        final NodeWorkspaceVersion sourceWsVersion = this.workspaceService.get( nodeId, WorkspaceContext.from( context ) );
        final NodeWorkspaceVersion targetWsVersion =
            this.workspaceService.get( nodeId, WorkspaceContext.from( this.target, context.getRepositoryId() ) );

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
        private Workspace target;

        private VersionService versionService;

        private WorkspaceService workspaceService;

        Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public B target( final Workspace target )
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
        public B workspaceService( final WorkspaceService workspaceService )
        {
            this.workspaceService = workspaceService;
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
