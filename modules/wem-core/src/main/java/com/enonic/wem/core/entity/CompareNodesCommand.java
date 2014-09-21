package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.CompareStatus;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NodeComparison;
import com.enonic.wem.api.entity.NodeComparisons;
import com.enonic.wem.api.entity.NodeVersion;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.compare.DiffStatusParams;
import com.enonic.wem.core.workspace.compare.DiffStatusResolver;

public class CompareNodesCommand
{
    private final EntityIds entityIds;

    private final Workspace target;

    private final Context context;

    private final WorkspaceService workspaceService;

    private final VersionService versionService;

    private CompareNodesCommand( Builder builder )
    {
        entityIds = builder.entityIds;
        target = builder.target;
        context = builder.context;
        workspaceService = builder.workspaceService;
        versionService = builder.versionService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeComparisons execute()
    {
        final NodeComparisons.Builder builder = NodeComparisons.create();

        for ( final EntityId entityId : this.entityIds )
        {
            final NodeComparison nodeComparison = doCompareVersions( entityId );

            builder.add( nodeComparison );
        }

        return builder.build();
    }

    private NodeComparison doCompareVersions( final EntityId entityId )
    {
        final NodeVersionId sourceVersionId = workspaceService.getCurrentVersion( entityId, WorkspaceContext.from( context ) );
        final NodeVersionId targetVersionId =
            workspaceService.getCurrentVersion( entityId, WorkspaceContext.from( this.target, this.context.getRepository() ) );

        final NodeVersion sourceVersion = getVersion( sourceVersionId );
        final NodeVersion targetVersion = getVersion( targetVersionId );

        final CompareStatus compareStatus = DiffStatusResolver.resolve( new DiffStatusParams( sourceVersion, targetVersion ) );

        return new NodeComparison( entityId, compareStatus );
    }

    private NodeVersion getVersion( final NodeVersionId nodeVersionId )
    {
        if ( nodeVersionId == null )
        {
            return null;
        }

        return versionService.getVersion( nodeVersionId, this.context.getRepository() );
    }


    public static final class Builder
    {
        private EntityIds entityIds;

        private Workspace target;

        private Context context;

        private WorkspaceService workspaceService;

        private VersionService versionService;

        private Builder()
        {
        }

        public Builder entityIds( final EntityIds entityIds )
        {
            this.entityIds = entityIds;
            return this;
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder context( final Context context )
        {
            this.context = context;
            return this;
        }

        public Builder workspaceService( final WorkspaceService workspaceService )
        {
            this.workspaceService = workspaceService;
            return this;
        }

        public Builder versionService( final VersionService versionService )
        {
            this.versionService = versionService;
            return this;
        }

        public CompareNodesCommand build()
        {
            return new CompareNodesCommand( this );
        }
    }
}
