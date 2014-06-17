package com.enonic.wem.core.workspace.compare;

import javax.inject.Inject;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.CompareStatus;
import com.enonic.wem.api.entity.EntityComparison;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.version.VersionBranch;
import com.enonic.wem.core.version.VersionBranchQuery;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.compare.query.CompareEntityQuery;
import com.enonic.wem.core.workspace.compare.query.CompareWorkspacesQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

public class WorkspaceCompareServiceImpl
    implements WorkspaceCompareService
{
    private WorkspaceService workspaceService;

    private VersionService versionService;

    @Override
    public WorkspaceComparison compareWorkspaces( final CompareWorkspacesQuery query )
    {
        final EntityIds allEntitiesWithDifference = workspaceService.getEntriesWithDiff( query );

        final WorkspaceComparison.Builder builder = WorkspaceComparison.create();

        final Workspace sourceWorkspace = query.getSource();
        final Workspace targetWorkspace = query.getTarget();

        for ( final EntityId entityId : allEntitiesWithDifference )
        {
            final EntityComparison entityComparison = doCompareEntity( entityId, sourceWorkspace, targetWorkspace );

            builder.add( entityComparison );
        }

        return builder.build();
    }

    @Override
    public EntityComparison compare( final CompareEntityQuery query )
    {
        return doCompareEntity( query.getEntityId(), query.getSource(), query.getTarget() );
    }

    private EntityComparison doCompareEntity( final EntityId entityId, final Workspace sourceWorkspace, final Workspace targetWorkspace )
    {
        final BlobKey sourceVersion = workspaceService.getById( new WorkspaceIdQuery( sourceWorkspace, entityId ) );
        final BlobKey targetVersion = workspaceService.getById( new WorkspaceIdQuery( targetWorkspace, entityId ) );

        final VersionBranch sourceBranch = getBranch( sourceVersion );
        final VersionBranch targetBranch = getBranch( targetVersion );

        final CompareStatus compareStatus = DiffStatusResolver.resolve( new DiffStatusParams( sourceBranch, targetBranch ) );

        return new EntityComparison( entityId, compareStatus );
    }

    private VersionBranch getBranch( final BlobKey version )
    {
        final VersionBranch branch;

        if ( version != null )
        {
            branch = versionService.getBranch( new VersionBranchQuery( version ) );
        }
        else
        {
            branch = VersionBranch.create().build();
        }
        return branch;
    }

    @Inject
    public void setWorkspaceService( final WorkspaceService workspaceService )
    {
        this.workspaceService = workspaceService;
    }

    @Inject
    public void setVersionService( final VersionService versionService )
    {
        this.versionService = versionService;
    }
}
