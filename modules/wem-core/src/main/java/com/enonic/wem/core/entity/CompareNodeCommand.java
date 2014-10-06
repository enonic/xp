package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.context.Context2;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.compare.DiffStatusParams;
import com.enonic.wem.core.workspace.compare.DiffStatusResolver;

public class CompareNodeCommand
{
    private final EntityId entityId;

    private final Workspace target;

    private final WorkspaceService workspaceService;

    private final VersionService versionService;

    private CompareNodeCommand( Builder builder )
    {
        entityId = builder.entityId;
        target = builder.target;
        workspaceService = builder.workspaceService;
        versionService = builder.versionService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeComparison execute()
    {
        final Context2 context = Context2.current();

        final NodeVersionId sourceVersionId = workspaceService.getCurrentVersion( entityId, WorkspaceContext.from( context ) );
        final NodeVersionId targetVersionId =
            workspaceService.getCurrentVersion( entityId, WorkspaceContext.from( this.target, context.getRepositoryId() ) );

        final NodeVersion sourceVersion = getVersion( sourceVersionId, context );
        final NodeVersion targetVersion = getVersion( targetVersionId, context );

        final CompareStatus compareStatus = DiffStatusResolver.resolve( new DiffStatusParams( sourceVersion, targetVersion ) );

        return new NodeComparison( entityId, compareStatus );
    }

    private NodeVersion getVersion( final NodeVersionId nodeVersionId, final Context2 context )
    {
        if ( nodeVersionId == null )
        {
            return null;
        }

        return versionService.getVersion( nodeVersionId, context.getRepositoryId() );
    }

    public static final class Builder
    {
        private EntityId entityId;

        private Workspace target;

        private WorkspaceService workspaceService;

        private VersionService versionService;

        private Builder()
        {
        }

        public Builder entityId( EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public Builder target( Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder workspaceService( WorkspaceService workspaceService )
        {
            this.workspaceService = workspaceService;
            return this;
        }

        public Builder versionService( VersionService versionService )
        {
            this.versionService = versionService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( entityId );
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( workspaceService );
            Preconditions.checkNotNull( versionService );
        }

        public CompareNodeCommand build()
        {
            this.validate();
            return new CompareNodeCommand( this );
        }
    }
}